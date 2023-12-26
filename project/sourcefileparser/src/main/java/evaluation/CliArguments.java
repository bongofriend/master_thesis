package evaluation;

public record CliArguments(String dataPath, String csvOutPutPath, boolean includeCKMetrics, String modelPath) {
}