package evaluation;

import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.util.Arrays;

public class CustomHeaderMappingStrategy<T> extends HeaderColumnNameMappingStrategy<T> {


    @Override
    public String[] generateHeader(T bean) throws CsvRequiredFieldEmptyException {
        var header = super.generateHeader(bean);
        return Arrays.stream(header)
                .map(s -> {
                   if(s.startsWith("NO")) {
                       return s;
                   }
                   return s.toLowerCase();
                })
                .toArray(String[]::new);
    }
}
