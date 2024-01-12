package evaluation;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.feature_extractors.FeatureExtractor;
import evaluation.feature_extractors.class_level.CountOfInterfacesFeatureExtractor;
import evaluation.feature_extractors.class_level.IsAbstractFeatureExtractor;
import evaluation.feature_extractors.class_level.IsPublicFeatureExtractor;
import evaluation.feature_extractors.class_level.IsStaticFeatureExtractor;
import evaluation.feature_extractors.construtor_level.CountOfPrivateConstructorsFeatureExtractor;
import evaluation.feature_extractors.field_level.*;
import evaluation.feature_extractors.method_level.*;

import java.util.Arrays;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class FeatureExtractorManager {
    private final FeatureExtractor[] featureExtractors;
    private final Map<String, ClassOrInterfaceDeclaration> allDeclarations;

    private final Logger logger;

    public FeatureExtractorManager(Map<String, ClassOrInterfaceDeclaration> allDeclarations) {
        this.allDeclarations = allDeclarations;
        this.logger = Logger.getLogger(FeatureExtractorManager.class.getSimpleName());
        this.featureExtractors = new FeatureExtractor[]{
                //Class Level Metrics
                new IsPublicFeatureExtractor(),
                new IsAbstractFeatureExtractor(),
                new IsPublicFeatureExtractor(),
                new IsStaticFeatureExtractor(),
                new CountOfInterfacesFeatureExtractor(),

                //Field Level Metrics
                new CountOfPrivateFieldsFeatureExtractor(),
                new CountOfStaticFieldsFeatureExtractor(),
                new CountOfFieldsFeatureExtractor(),
                new CountOfObjectFieldsFeatureExtractor(),
                new CountOfOtherClassesWithOwnFieldTypeFeatureExtractor(),


                //Constructor Level Metrics
                new CountOfPrivateConstructorsFeatureExtractor(),

                //Method Level Metrics
                new CountOfAbstractMethodsFeatureExtractor(),
                new CountOfPrivateMethodsFeatureExtractor(),
                new CountOfStaticMethodsFeatureExtractor(),
                new CountOfMethodsFeatureExtractor(),

                new CountOfOverriddenMethodsFeatureExtractor()

        };
    }

    private ClassMetricVector[] extract(SourceFileStreamer.Value el) {
        for (var vector : el.vector()) {
            if (!el.classOrInterfaceDeclaration().containsKey(vector.getEntity())) {
                vector.prepare();
                continue;
            }
            var currentDeclaration = el.classOrInterfaceDeclaration().get(vector.getEntity());
            for (var f : featureExtractors) {
                var feature = f.extract(currentDeclaration, el.classOrInterfaceDeclaration(), el.vector(), allDeclarations);
                vector.addMetric(f.getFeatureName(), feature);
            }
            vector.prepare();
        }
        return el.vector();
    }

    public Stream<ClassMetricVector> extractFeatures(Stream<SourceFileStreamer.Value> elementStream) {
        logger.info("Starting to extract features");
        return elementStream
                .map(this::extract)
                .flatMap(Arrays::stream);

    }

}
