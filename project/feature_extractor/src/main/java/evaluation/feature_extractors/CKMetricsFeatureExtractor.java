package evaluation.feature_extractors;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKClassResult;
import com.github.mauricioaniche.ck.CKNotifier;
import evaluation.ClassMetricVector;
import evaluation.CliArguments;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class CKMetricsFeatureExtractor {

    private final String projectsPath;
    private final CK ckAnalyzer;
    private final Listener listener;
    private final Logger logger;
    public CKMetricsFeatureExtractor(CliArguments arguments) {
        this.projectsPath = arguments.projectsPath();
        this.listener = new Listener();
        this.ckAnalyzer = new CK();
        this.logger = Logger.getLogger(CKMetricsFeatureExtractor.class.getSimpleName());
        loadProjects();
    }

    private void loadProjects() {
        try (var projectStream = Files.list(Paths.get(projectsPath))) {
            projectStream
                    .filter(Files::isDirectory)
                    .filter(path -> !path.toString().contains("java"))
                    .forEach(p -> {
                        logger.info(String.format("Loading project in path '%s'", p));
                        ckAnalyzer.calculate(p, listener);
                    });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Stream<ClassMetricVector> calculateMetrics(Stream<ClassMetricVector> stream) {
        logger.info("Extracting CK metrics");
        return stream.peek(el -> Optional.of(el)
                .stream()
                .filter(v -> listener.results.containsKey(v.getEntity()))
                .forEach(vector -> {
                    var ckMetrics = listener.results.get(vector.getEntity());
                    vector.setCouplingBetweenObjects(ckMetrics.getCbo());
                    vector.setDepthOfInheritance(ckMetrics.getDit());
                    vector.setResponseForAClass(ckMetrics.getRfc());
                    vector.setWeightedMethodClass(ckMetrics.getWmc());
                    vector.setTightClassCohesion(ckMetrics.getTightClassCohesion());
                    vector.setLackOfCohesionOfMethods(ckMetrics.getLcomNormalized());
                }));
    }

    private class Listener implements CKNotifier {
        public Map<String, CKClassResult> results;

        public Listener() {
            results = new HashMap<>();
        }

        @Override
        public void notify(CKClassResult ckClassResult) {
            results.put(ckClassResult.getClassName(), ckClassResult);
        }

        @Override
        public void notifyError(String sourceFilePath, Exception e) {
            CKNotifier.super.notifyError(sourceFilePath, e);
        }
    }
}
