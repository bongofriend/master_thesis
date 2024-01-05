package evaluation;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class SourceFileStreamer {
    public record Element(ClassMetricVector[] vector, Map<String, ClassOrInterfaceDeclaration> classOrInterfaceDeclaration) {}

    private final String datasetPath;
    private final Logger logger;
    private final JavaParser parser;

    public SourceFileStreamer(CliArguments args) {
        this.datasetPath = args.dataPath();
        this.logger = Logger.getLogger(SourceFileStreamer.class.getSimpleName());
        this.parser = new JavaParser();
        this.parser
                .getParserConfiguration()
                .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_6);
    }

    public Stream<Element> streamMicroArchitectures() {
        logger.info("Start parsing micro architectures");
        Stream.Builder<Element> builder = Stream.builder();
        try(var microArchStream = Files.walk(Paths.get(datasetPath), 3, FileVisitOption.FOLLOW_LINKS)) {
            microArchStream
                    .filter(Files::isDirectory)
                    .filter(p -> p.toString().contains("micro_arch"))
                    .forEach(p -> {
                        try {
                            builder.add(parseMicroArch(p));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
            return builder.build();
        } catch (Exception ex) {
            logger.severe(ex.getMessage());
            return Stream.empty();
        }
    }

    private Element parseMicroArch(Path microArchPath) throws IOException{
        var designPattern = microArchPath.getParent().toFile().getName();
        var microArchName = microArchPath.toFile().getName();
        var projectName = Files.readString(microArchPath.resolve("project.txt"));
        var classMetricVectors = prefillClassMetricVectors(designPattern, microArchName, projectName, microArchPath);
        var classes = extractClassesInMicroArchPath(microArchPath);
        return new Element(classMetricVectors, classes);

    }

    private ClassMetricVector[] prefillClassMetricVectors(String designPattern, String microArchName, String projectName, Path microArchPath) throws FileNotFoundException {
        var rolesFile = microArchPath.resolve("roles.csv");
        var reader = new BufferedReader(new FileReader(rolesFile.toFile()));
        var roles = new CsvToBeanBuilder<RoleEntry>(reader)
                .withType(RoleEntry.class)
                .withSeparator('|')
                .build()
                .parse();
        return roles
                .stream()
                .map(r -> new ClassMetricVector(
                        r.role(),
                        r.roleKind(),
                        r.entity(),
                        designPattern,
                        microArchName,
                        projectName
                ))
                .toArray(ClassMetricVector[]::new);
    }

    private Map<String, ClassOrInterfaceDeclaration> extractClassesInMicroArchPath(Path microArchPath) throws IOException {
        var classes = new HashMap<String, ClassOrInterfaceDeclaration>();
        try(var stream = Files.list(microArchPath)) {
            stream
                    .filter(Files::isRegularFile)
                    .filter(f -> f.toString().endsWith(".java"))
                    .forEach(p -> {
                        try {
                            extractClassesFromSourceFile(p, classes);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        return classes;
    }

    private void extractClassesFromSourceFile(Path p, Map<String, ClassOrInterfaceDeclaration> classes) throws FileNotFoundException {
        ParseResult<CompilationUnit> result;
        var file = new FileReader(p.toFile());
        result = parser.parse(file);

        if(result.getResult().isEmpty()) {
            return;
        }
        var compilationUnit = result.getResult().get();
        compilationUnit
                .findAll(ClassOrInterfaceDeclaration.class)
                .stream()
                .filter(c -> c.getFullyQualifiedName().isPresent())
                .forEach(c -> {
                    classes.put(c.getFullyQualifiedName().get(), c);
                });
    }
}
