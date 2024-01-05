import evaluation.ClassMetricVectorWriter;
import evaluation.CliArguments;
import evaluation.FeatureExtractorManager;
import evaluation.SourceFileStreamer;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.nio.file.Paths;
import java.util.Arrays;

public class Main {
    public static void main(String[] args)  {
        CliArguments arguments = null;
        try {
            arguments = parseCmdArguments(args);
            var sourceFileStreamer = new SourceFileStreamer(arguments);
            var classMetricVectorWriter = new ClassMetricVectorWriter(arguments);
            var featureExtractor = new FeatureExtractorManager();
            var vectors = sourceFileStreamer
                    .streamMicroArchitectures()
                    .map(featureExtractor::extractFeatures)
                    .flatMap(Arrays::stream)
                    .toList();
            classMetricVectorWriter.writeClassMetricVectors(vectors);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static CliArguments parseCmdArguments(String[] args) throws ParseException {
        var options = new Options();
        var sourceFilesDirOption = Option.builder("s")
                .longOpt("sourceFilesDir")
                .required(true)
                .type(String.class)
                .hasArg()
                .desc("Path of Directory for source files to parse")
                .build();

        var csvOutputPathOption = Option.builder("o")
                .longOpt("csvOutputPathOption")
                .required(true)
                .type(String.class)
                .hasArg()
                .desc("Path of CSV file where results are written")
                .build();

        var includeCKMetricsOption = Option.builder("ck")
                .longOpt("includeCKMetrics")
                .required()
                .desc("Set to include CK metrics")
                .hasArg()
                .type(Boolean.class)
                .build();
        var modelPathOption = Option.builder("m")
                .longOpt("modelPath")
                .required()
                .hasArg()
                .type(String.class)
                .build();

        options.addOption(sourceFilesDirOption);
        options.addOption(csvOutputPathOption);
        options.addOption(includeCKMetricsOption);
        options.addOption(modelPathOption);

        var parser = new DefaultParser();
        var cli = parser.parse(options, args);
        return new CliArguments(
                Paths.get(cli.getOptionValue(sourceFilesDirOption)).toAbsolutePath().normalize().toString(),
                Paths.get(cli.getOptionValue(csvOutputPathOption)).toAbsolutePath().normalize().toString(),
                Boolean.parseBoolean(cli.getOptionValue(includeCKMetricsOption)),
                Paths.get(cli.getOptionValue(modelPathOption)).toAbsolutePath().normalize().toString()
        );
    }
}
