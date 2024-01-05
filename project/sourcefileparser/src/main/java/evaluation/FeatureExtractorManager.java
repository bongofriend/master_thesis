package evaluation;

import evaluation.feature_extractors.*;
import evaluation.feature_extractors.method_level.*;

import java.util.LinkedList;

public class FeatureExtractorManager {
    private final FeatureExtractor[] featureExtractors;

    public FeatureExtractorManager() {
        this.featureExtractors = new FeatureExtractor[]{
                //Class Level Metrics
                /*new IsPublicFeatureExtractor(),
                new IsAbstractFeatureExtractor(),
                new IsPublicFeatureExtractor(),
                new IsStaticFeatureExtractor(),
                new ExtendsEntityFeatureExtractor(),
                new CountOfInterfacesFeatureExtractor(),
                new ExtendsEntityInMicroArchitectureFeatureExtractor(),*/

                //Field Level Metrics
                /*new CountOfPrivateFieldsFeatureExtractor(),
                new CountOfStaticFieldsFeatureExtractor(),
                new CountOfReferenceInFieldsFeatureExtractor()*/

                //Constructor Level Metrics
                /*new CountOfReferenceAsConstructorParameter()
                new CountOfPrivateConstructorsFeatureExtractor()*/

                //TODO: Test Feature Extractors from here on
                //Method Level Metrics
                /*new CountOfAbstractMethodsFeatureExtractor(),
                new CountOfPrivateMethodsFeatureExtractor(),
                new CountOfReferenceAsMethodParameterFeatureExtractor(),
                new CountOfReferenceAsReturnTypeFeatureExtractor(),
                new CountOfReferenceAsVariableFeatureExtractor(),
                new CountOfStaticMethodsFeatureExtractor(),
                new CountOfReferenceAsMethodInvocationFeatureExtractor()*/

        };
    }

    public ClassMetricVector[] extractFeatures(SourceFileStreamer.Element el) {
        var finishedVectors = new LinkedList<ClassMetricVector>();
        for (var vector : el.vector()) {
            if (!el.classOrInterfaceDeclaration().containsKey(vector.getEntity())) {
                vector.prepare();
                continue;
            }
            var currentDeclaration = el.classOrInterfaceDeclaration().get(vector.getEntity());
            for (var f : featureExtractors) {
                var feature = f.extract(currentDeclaration, el.classOrInterfaceDeclaration(), el.vector());
                vector.addMetric(f.getFeatureName(), feature);
            }
            vector.prepare();
            finishedVectors.add(vector);
        }
        return finishedVectors.toArray(ClassMetricVector[]::new);
    }

}
