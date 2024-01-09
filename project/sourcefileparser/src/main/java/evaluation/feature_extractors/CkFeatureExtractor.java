package evaluation.feature_extractors;

import com.github.mauricioaniche.ck.CK;
import com.github.mauricioaniche.ck.CKClassResult;
import com.github.mauricioaniche.ck.CKNotifier;
import evaluation.ClassMetricVector;
import evaluation.CliArguments;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CkFeatureExtractor {

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

    private final String projectsPath;
    private final CK ckAnalyzer;


    public CkFeatureExtractor(String projectsPath) {
        this.projectsPath = projectsPath;
        this.ckAnalyzer = new CK();
    }

    public ClassMetricVector[] calculateMetrics(ClassMetricVector[] classMetricVectors) {
        var classMetricVectorMap = Arrays.stream(classMetricVectors).collect(Collectors.toMap(ClassMetricVector::getEntity, v -> v));
        var listener = new Listener();
        for(var v: classMetricVectors) {
            var path = resolvePath(v);
            if(path.isEmpty()) {
                continue;
            }
            ckAnalyzer.calculate(path.get(), listener);
        }
        for(var e: listener.results.keySet()) {
            if(!classMetricVectorMap.containsKey(e)) {
                continue;
            }
            //TODO Add values
        }
        return classMetricVectors;
    }


    private Optional<Path> resolvePath(ClassMetricVector vector) {
        List<String> processedSegments = new ArrayList<>();
        for(var segment: vector.getEntity().split("\\.")) {
            processedSegments.add(segment);
            if(!Character.isUpperCase(segment.charAt(0))) {
                continue;
            }
            var filePath = String.format("%s.java", String.join(File.separator,  processedSegments));
            var path = Paths.get(projectsPath, vector.getProject(), filePath);
            if(Files.exists(path) && Files.isRegularFile(path)) {
                return Optional.of(path);
            }
        }
        return Optional.empty();
    }
}
