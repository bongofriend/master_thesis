import com.opencsv.exceptions.CsvException;
import evaluation.MetricGatherer;
import org.apache.commons.cli.*;

import java.io.IOException;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws IOException, CsvException {
        try {
            var options = parseCmdArguments(args);
            var gatherer = new MetricGatherer(options[0], options[1]);
            gatherer.parseDataset();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

    }

    private static String[] parseCmdArguments(String[] args) throws ParseException {
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
        options.addOption(sourceFilesDirOption);
        options.addOption(csvOutputPathOption);
        var parser = new DefaultParser();
        var cli = parser.parse(options, args);
        if(!cli.hasOption(sourceFilesDirOption)) {
            return new String[]{};
        }
        return new String[]{
                cli.getOptionValue(sourceFilesDirOption),
                cli.getOptionValue(csvOutputPathOption)
        };
    }
}
