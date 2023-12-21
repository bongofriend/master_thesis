import com.opencsv.exceptions.CsvException;
import evaluation.CliArguments;
import evaluation.Parser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args)  {
        CliArguments arguments = null;
        try {
            arguments = parseCmdArguments(args);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        var gatherer = new Parser(arguments);
        gatherer.parseDataset();
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

        options.addOption(sourceFilesDirOption);
        options.addOption(csvOutputPathOption);
        options.addOption(includeCKMetricsOption);

        var parser = new DefaultParser();
        var cli = parser.parse(options, args);
        return new CliArguments(
                Paths.get(cli.getOptionValue(sourceFilesDirOption)).toAbsolutePath().normalize().toString(),
                Paths.get(cli.getOptionValue(csvOutputPathOption)).toAbsolutePath().normalize().toString(),
                Boolean.parseBoolean(cli.getOptionValue(includeCKMetricsOption))
        );
    }
}
