package evaluation;

import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomHeaderMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T> {

    private final Set<String> headersToExclude;

    //role,role_kind,entity,design_pattern,micro_architecture,project,
    public CustomHeaderMappingStrategy() {
        headersToExclude = new HashSet<>(List.of("design_pattern", "micro_architecture", "role", "role_kind", "entity", "project"));
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
