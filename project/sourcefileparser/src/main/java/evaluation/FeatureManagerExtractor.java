package evaluation;

import evaluation.featureextractors.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FeatureManagerExtractor {
    private final FeatureExtractor[] featureExtractors;

    public FeatureManagerExtractor() {
        this.featureExtractors = new FeatureExtractor[] {
            new IsPublicFeatureExtractor(),
            new IsAbstractFeatureExtractor(),
            new IsPublicFeatureExtractor(),
            new IsStaticFeatureExtractor(),
            new ExtendsEntityFeatureExtractor(),
            new CountOfInterfacesFeatureExtractor()
        };
    }
    public ClassMetricVector[] extractFeatures(SourceFileStreamer.Element el) {
        var finishedVectors = new LinkedList<ClassMetricVector>();
        for(var vector: el.vector()) {
            if(!el.classOrInterfaceDeclaration().containsKey(vector.getEntity())) {
                vector.prepare();
                continue;
            }
            var currentDeclaration = el.classOrInterfaceDeclaration().get(vector.getEntity());
            for(var f: featureExtractors) {
                vector.addMetric(f.getFeatureName(), f.extract(currentDeclaration, el.classOrInterfaceDeclaration()));
            }
            vector.prepare();
            finishedVectors.add(vector);
        }
        return finishedVectors.toArray(ClassMetricVector[]::new);
    }

}
