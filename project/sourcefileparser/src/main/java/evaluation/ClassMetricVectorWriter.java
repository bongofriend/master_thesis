package evaluation;

import com.opencsv.CSVWriter;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassMetricVectorWriter {

    private static class CustomHeaderMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T> {

        private final Set<String> headersToExclude;

        //role,role_kind,entity,design_pattern,micro_architecture,project,
        public CustomHeaderMappingStrategy() {
            this.headersToExclude = new HashSet<>(List.of(
                    ClassMetricVectorConstants.DESIGN_PATTERN,
                    ClassMetricVectorConstants.MICRO_ARCHITECTURE,
                    ClassMetricVectorConstants.ROLE_KIND,
                    ClassMetricVectorConstants.ENTITY,
                    ClassMetricVectorConstants.PROJECT,
                    ClassMetricVectorConstants.ROLE
            ));
        }

        @Override
        public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
            var header = super.generateHeader(bean);
            return Arrays.stream(header)
                    .map(s -> {
                        if (headersToExclude.contains(s.toLowerCase())) {
                            return s.toLowerCase();
                        }
                        return s.toUpperCase();
                    })
                    .toArray(String[]::new);
        }
    }
    private final String csvPath;

    public ClassMetricVectorWriter(CliArguments args) {
        this.csvPath = args.csvOutPutPath();
    }

    public void writeClassMetricVectors(List<ClassMetricVector> vectors) {
        StatefulBeanToCsv<ClassMetricVector> csvWriter;
        BufferedWriter bufferedWriter;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(csvPath));
            var strategy = new CustomHeaderMappingStrategy<ClassMetricVector>();
            strategy.setType(ClassMetricVector.class);
            csvWriter = new StatefulBeanToCsvBuilder<ClassMetricVector>(bufferedWriter)
                    .withMappingStrategy(strategy)
                    .withSeparator(',')
                    .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                    .build();
            csvWriter.write(vectors);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (CsvRequiredFieldEmptyException | CsvDataTypeMismatchException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
