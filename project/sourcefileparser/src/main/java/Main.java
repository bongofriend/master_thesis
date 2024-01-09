import evaluation.ClassMetricVectorWriter;
import evaluation.CliArguments;
import evaluation.FeatureExtractorManager;
import evaluation.SourceFileStreamer;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {
    public static void main(String[] args)  {
        CliArguments arguments = null;
        try {
            arguments = parseCmdArguments(args);
            var sourceFileStreamer = new SourceFileStreamer(arguments);
            var classMetricVectorWriter = new ClassMetricVectorWriter(arguments);
            var featureExtractor = new FeatureExtractorManager(arguments, sourceFileStreamer.getEntityToDeclaration());
            var sourceFiles = sourceFileStreamer.streamMicroArchitectures();
            classMetricVectorWriter.write(featureExtractor
                    .extractFeatures(sourceFiles));
        } catch (ParseException | IOException e) {
            throw new RuntimeException(e);
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
