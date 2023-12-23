package evaluation;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKClassResult;
import com.github.mauricioaniche.ck.CKNotifier;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class CKAnalyzer {

    private final Logger logger;

    public CKAnalyzer() {
        logger = Logger.getLogger(CKAnalyzer.class.getSimpleName());
    }

    public Map<String, CKMetrics> getCKMetrics(Path microArchPath) {
        var analyzer = new CK();
        var listener = new ResultNotificationHandler(logger);
        analyzer.calculate(microArchPath, listener);
        return listener.toMetrics();
    }

    public record CKMetrics(float cbo, float fanIn, float fanOut, float noc, float rfc, float tcc, float lcc) {
    }

    private class ResultNotificationHandler implements CKNotifier {
        private final Logger logger;
        private final Map<String, CKClassResult> results;

        public ResultNotificationHandler(Logger logger) {
            this.results = new HashMap<>();
            this.logger = logger;
        }

        @Override
        public void notify(CKClassResult ckClassResult) {
            results.put(ckClassResult.getClassName(), ckClassResult);
        }

        @Override
        public void notifyError(String sourceFilePath, Exception e) {
            logger.severe(e.getMessage());
            CKNotifier.super.notifyError(sourceFilePath, e);
        }

        public Map<String, CKMetrics> toMetrics() {
            var resultMap = new HashMap<String, CKMetrics>();
            for (var entry : results.entrySet()) {
                var result = entry.getValue();
                resultMap.put(entry.getKey(), new CKMetrics(result.getCbo(),
                        result.getFanin(),
                        result.getFanout(),
                        result.getNoc(),
                        result.getRfc(),
                        Float.isNaN(result.getTightClassCohesion()) ? 0f : result.getTightClassCohesion(),
                        Float.isNaN(result.getLooseClassCohesion()) ? 0f : result.getLooseClassCohesion()
                ));
            }
            return resultMap;
        }
    }
}
