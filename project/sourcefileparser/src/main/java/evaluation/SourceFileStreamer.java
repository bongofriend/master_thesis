package evaluation;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.utils.SourceRoot;
import com.opencsv.bean.CsvToBeanBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class SourceFileStreamer {
    private final String rolesCSVPath;

    public record Element(ClassMetricVector[] vector, Map<String, ClassOrInterfaceDeclaration> classOrInterfaceDeclaration) { }

    private final String projectsPath;
    private final Logger logger;
    private final Map<String, List<ClassMetricVector>> microArchToClassMetricVectors;

    public Map<String, ClassOrInterfaceDeclaration> getEntityToDeclaration() {
        return entityToDeclaration;
    }

    private final Map<String, ClassOrInterfaceDeclaration> entityToDeclaration;



    public SourceFileStreamer(CliArguments args) {
        this.projectsPath = args.projectsPath();
        this.rolesCSVPath = args.rolesCSVPath();
        this.logger = Logger.getLogger(SourceFileStreamer.class.getSimpleName());
        this.microArchToClassMetricVectors = new HashMap<>();
        this.entityToDeclaration = new HashMap<>();
    }

    public Stream<Element> streamMicroArchitectures() throws IOException {
        logger.info("Start parsing micro architectures");
        parseRolesFile();
        loadProjects();
        return microArchToClassMetricVectors.keySet()
                .stream()
                .map(microArchToClassMetricVectors::get)
                .map(vectors -> {
                    var declarationsMap = new HashMap<String, ClassOrInterfaceDeclaration>();
                    for(var v: vectors) {
                        var key = getEntityKey(v.getProject(), v.getEntity());
                        if(!entityToDeclaration.containsKey(key)) {
                            continue;
                        }
                        declarationsMap.put(v.getEntity(), entityToDeclaration.get(key));
                    }
                    return new SourceFileStreamer.Element(vectors.toArray(ClassMetricVector[]::new), declarationsMap);
                });

    }

    private void parseRolesFile() throws FileNotFoundException {
        var csvReader = new BufferedReader(new FileReader(rolesCSVPath));
        new CsvToBeanBuilder<SourceFile>(csvReader)
                .withType(SourceFile.class)
                .withSeparator(',')
                .build()
                .parse()
                .forEach(s -> {
                    microArchToClassMetricVectors.putIfAbsent(s.getMicroArchitecture(), new LinkedList<>());
                    microArchToClassMetricVectors.get(s.getMicroArchitecture()).add(
                            new ClassMetricVector(
                                    s.getRole(),
                                    s.getRole(),
                                    s.getEntity(),
                                    s.getDesignPattern(),
                                    s.getMicroArchitecture(),
                                    s.getProject()
                            )
                    );
                });

    }

    private void loadProjects() {
        var projects = Paths.get(projectsPath);
       try(var projectDirs = Files.list(projects)) {
           for (var p: projectDirs.toList()) {
               Stream.of(p)
                       .filter(Files::isDirectory)
                       .map(path -> {
                           var parserConfig = new ParserConfiguration()
                                   .setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_6)
                                   .setSymbolResolver(new JavaSymbolSolver(new JavaParserTypeSolver(path)));
                           var sourceRoot = new SourceRoot(path.toAbsolutePath(), parserConfig);
                           try {
                               sourceRoot.tryToParse();
                           } catch (IOException e) {
                               throw new RuntimeException(e);
                           }
                           return sourceRoot.getCompilationUnits();
                       })
                       .flatMap(List::stream)
                       .map(cu -> cu.findAll(ClassOrInterfaceDeclaration.class))
                       .flatMap(List::stream)
                       .filter(cls -> cls.getFullyQualifiedName().isPresent())
                       .forEach(cls -> {
                           var projectName = p.getFileName().toString();
                           var entityName = cls.getFullyQualifiedName().get();
                           entityToDeclaration.put(getEntityKey(projectName, entityName), cls);
                       });
           }
       } catch (IOException ex) {
           logger.severe(ex.getMessage());
       }

    }

    private String getEntityKey(String project, String entity) {
        if(entity.startsWith("java") || entity.startsWith("javax")) {
            project = "java";
        }
        return String.format("%s-%s", project, entity);
    }
}
