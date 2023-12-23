package evaluation;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.opencsv.CSVWriter;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import evaluation.metricevaluations.*;

import java.io.*;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

public class Parser {
    private final MetricEvaluation[] metricEvaluations;
    private final Map<String, ClassOrInterfaceDeclaration> classDeclarationPool;
    private final Logger logger;
    private final CKAnalyzer analyzer;
    private final JavaParser parser;

    private final CliArguments arguments;

    public Parser(CliArguments arguments) {
        this.metricEvaluations = new MetricEvaluation[]{
                new NumberOfAbstractMethodsEvaluation(),
                new NumberOfFieldsMetricEvaluation(),
                new NumberOfMethodsEvaluation(),
                new NumberOfObjectFieldsEvaluation(),
                new NumberOfOtherClassesWithFieldOfOwnTypeEvaluation(),
                new NumberOfOverriddenMethodsEvaluation(),
                new NumberOfPrivateConstructorsEvaluation(),
                new NumberOfStaticFieldsEvaluation(),
                new NumberOfStaticMethodsEvaluation()
        };
        this.arguments = arguments;
        this.classDeclarationPool = new HashMap<>();
        this.logger = Logger.getLogger(Parser.class.getSimpleName());
        this.parser = new JavaParser();
        this.parser.getParserConfiguration().setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_6);
        this.analyzer = new CKAnalyzer();
    }


    public void parseDataset() {
        logger.info(String.format("Starting parsing dataset in %s", arguments.dataPath()));
        var path = Paths.get(arguments.dataPath());
        try (var stream = Files.walk(path, 3, FileVisitOption.FOLLOW_LINKS)) {
            var writer = new BufferedWriter(new FileWriter(arguments.csvOutPutPath()));
            var strategy = new CustomHeaderMappingStrategy<MetricEvaluationResult>();
            strategy.setType(MetricEvaluationResult.class);
            var csvWriterStream = new StatefulBeanToCsvBuilder<MetricEvaluationResult>(writer)
                    .withMappingStrategy(strategy)
                    .withSeparator(',')
                    .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                    .build();
            var results = stream
                    .filter(Files::isDirectory)
                    .filter(p -> p.toString().contains("micro_arch"))
                    .map(microArchPath -> {
                        try {
                            return evaluateMicroArchitectureDirectory(microArchPath);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    })
                    .flatMap(List::stream)
                    .toList();
            csvWriterStream.write(results);
            writer.flush();
            writer.close();
        } catch (IOException | CsvRequiredFieldEmptyException | CsvDataTypeMismatchException e) {
            logger.severe(e.getMessage());
        }
        logger.info(String.format("Results written to %s", arguments.csvOutPutPath()));
    }

    private List<MetricEvaluationResult> evaluateMicroArchitectureDirectory(Path microArchPath) throws IOException {
        var rolesFile = microArchPath.resolve("roles.csv");
        var designPattern = microArchPath.getParent().toFile().getName();
        var microArchName = microArchPath.toFile().getName();
        var projectName = Files.readString(microArchPath.resolve("project.txt"));
        logger.info(String.format("Parsing micro architecture %s in design pattern %s", microArchName, designPattern));
        BufferedReader reader = new BufferedReader(new FileReader(rolesFile.toFile()));
        var roles = new CsvToBeanBuilder<RoleEntry>(reader)
                .withType(RoleEntry.class)
                .withSeparator('|')
                .build()
                .parse();

        return parseSourceFiles(designPattern, microArchName, projectName, microArchPath, roles);
    }

    private List<MetricEvaluationResult> parseSourceFiles(String designPattern, String microArchitecture, String project, Path microArchPath, List<RoleEntry> roles) throws IOException {
        var classList = new LinkedList<ClassOrInterfaceDeclaration>();
        var results = new HashMap<String, MetricEvaluationResult.Builder>();
        var roleEntityMap = new HashMap<String, RoleEntry>();

        for (var r : roles) {
            roleEntityMap.put(r.entity(), r);
        }
        for (var p : getSourceFilePaths(microArchPath)) {
            classList.addAll(extractClass(p, roleEntityMap.keySet()));
        }
        for (var c : classList) {
            var name = c.getFullyQualifiedName();
            if (name.isEmpty()) {
                continue;
            }
            var role = roleEntityMap.get(name.get());
            var builder = new MetricEvaluationResult.Builder(role, microArchitecture, designPattern, project);
            for (var e : metricEvaluations) {
                builder.addMetric(e.getMetricName(), e.evaluate(c, this, roleEntityMap.keySet()));
            }
            results.put(name.get(), builder);
        }
        if (arguments.includeCKMetrics()) {
            var ckMetrics = analyzer.getCKMetrics(microArchPath);
            for (var e : ckMetrics.entrySet()) {
                var name = e.getKey();
                var ckMetric = e.getValue();
                var builder = results.get(name);
                if(builder == null)
                    continue;
                builder.addMetric("CBO", ckMetric.cbo());
                builder.addMetric("FAN_IN", ckMetric.fanIn());
                builder.addMetric("FAN_OUT", ckMetric.fanOut());
                builder.addMetric("NOC", ckMetric.noc());
                builder.addMetric("RFC", ckMetric.rfc());
                builder.addMetric("TCC", ckMetric.tcc());
                builder.addMetric("LCC", ckMetric.lcc());
            }
        }
        return results
                .values()
                .stream()
                .map(m -> m.toMetricEvaluationResult(arguments.includeCKMetrics()))
                .toList();
    }

    private List<ClassOrInterfaceDeclaration> extractClass(Path p, Set<String> entityNames) throws FileNotFoundException {
        ParseResult<CompilationUnit> compilationUnitParseResult;
        List<ClassOrInterfaceDeclaration> classesToParse = new ArrayList<>();
        var reader = new FileReader(p.toFile());
        compilationUnitParseResult = parser.parse(reader);
        if (compilationUnitParseResult.getResult().isEmpty()) {
            return new ArrayList<>();
        }

        var classList = compilationUnitParseResult
                .getResult().get()
                .findAll(ClassOrInterfaceDeclaration.class);
        for (var c : classList) {
            var name = c.getFullyQualifiedName();
            if (name.isEmpty() || !entityNames.contains(name.get())) {
                continue;
            }
            classesToParse.add(c);
            classDeclarationPool.put(name.get(), c);
        }
        return classesToParse;
    }

    public ClassOrInterfaceDeclaration getCompilationUnit(String name) {
        return classDeclarationPool.get(name);
    }

    private List<Path> getSourceFilePaths(Path microArchPath) {
        try (var stream = Files.list(microArchPath)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(file -> file.toString().endsWith(".java"))
                    .toList();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
