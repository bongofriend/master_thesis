import evaluation.ClassMetricVectorWriter;
import evaluation.CliArguments;
import evaluation.FeatureExtractorManager;
import evaluation.SourceFileStreamer;
import evaluation.feature_extractors.CKMetricsFeatureExtractor;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class Main {
    public static void main(String[] args) {
        CliArguments arguments;
        var logger = Logger.getLogger(Main.class.getSimpleName());
        try {
            arguments = parseCmdArguments(args);
            var classMetricVectorWriter = new ClassMetricVectorWriter(arguments);
            var objs = initialize(arguments);
            var sourceFileStreamer = (SourceFileStreamer) objs[0];
            var ckMetricsFeatureExtractor = (CKMetricsFeatureExtractor) objs[1];
            var featureExtractor = new FeatureExtractorManager(sourceFileStreamer.getEntityToDeclaration());
            var sourceFiles = sourceFileStreamer.streamMicroArchitectures();
            var processedStream = featureExtractor.extractFeatures(sourceFiles);
            processedStream = ckMetricsFeatureExtractor.calculateMetrics(processedStream);
            classMetricVectorWriter.write(processedStream);
        } catch (ParseException | IOException e) {
            logger.severe(e.getMessage());
        }
    }


    private static Object[] initialize(CliArguments arguments) {
        try (var service = Executors.newFixedThreadPool(2)) {
            var sourceFileStreamerTask = CompletableFuture.supplyAsync(() -> new SourceFileStreamer(arguments), service);
            var ckMetricsFeatureExtractor = CompletableFuture.supplyAsync(() -> new CKMetricsFeatureExtractor(arguments), service);
            var combined = CompletableFuture.allOf(sourceFileStreamerTask, ckMetricsFeatureExtractor);
            combined.join();

            return new Object[]{sourceFileStreamerTask.join(), ckMetricsFeatureExtractor.join()};
        }
    }

    private static CliArguments parseCmdArguments(String[] args) throws ParseException {
        var options = new Options();
        var projectsPath = Option.builder("p")
                .longOpt("projectsPath")
                .required(true)
                .type(String.class)
                .hasArg()
                .desc("Path to unzipped project source files")
                .build();

        var outputCSVPath = Option.builder("o")
                .longOpt("outputCSVPath")
                .required(true)
                .type(String.class)
                .hasArg()
                .desc("Path of CSV file where results are written")
                .build();

        var rolesCSVPath = Option.builder("r")
                .longOpt("rolesCSVPath")
                .required(true)
                .hasArg()
                .type(String.class)
                .desc("Path of extracted roles")
                .build();


        options.addOption(projectsPath);
        options.addOption(outputCSVPath);
        options.addOption(rolesCSVPath);

        var parser = new DefaultParser();
        var cli = parser.parse(options, args);
        return new CliArguments(
                Paths.get(cli.getOptionValue(rolesCSVPath)).toAbsolutePath().normalize().toString(),
                Paths.get(cli.getOptionValue(projectsPath)).toAbsolutePath().normalize().toString(),
                Paths.get(cli.getOptionValue(outputCSVPath)).toAbsolutePath().normalize().toString()
        );
    }
}
