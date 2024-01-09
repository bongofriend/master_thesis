package evaluation;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.feature_extractors.*;
import evaluation.feature_extractors.class_level.*;
import evaluation.feature_extractors.construtor_level.CountOfPrivateConstructorsFeatureExtractor;
import evaluation.feature_extractors.construtor_level.CountOfReferenceAsConstructorParameter;
import evaluation.feature_extractors.field_level.*;
import evaluation.feature_extractors.method_level.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.Stream;

public class FeatureExtractorManager {
    private final FeatureExtractor[] featureExtractors;
    private final Map<String, ClassOrInterfaceDeclaration> allDeclarations;

    private final String projectsPath;
    public FeatureExtractorManager(CliArguments args, Map<String, ClassOrInterfaceDeclaration> allDeclarations) {
        this.allDeclarations = allDeclarations;
        this.projectsPath = args.projectsPath();
        this.featureExtractors = new FeatureExtractor[]{
                //Class Level Metrics
                new IsPublicFeatureExtractor(),
                new IsAbstractFeatureExtractor(),
                new IsPublicFeatureExtractor(),
                new IsStaticFeatureExtractor(),
                new ExtendsEntityFeatureExtractor(),
                new CountOfInterfacesFeatureExtractor(),
                new ExtendsEntityInMicroArchitectureFeatureExtractor(),

                //Field Level Metrics
                new CountOfPrivateFieldsFeatureExtractor(),
                new CountOfStaticFieldsFeatureExtractor(),
                new CountOfReferenceInFieldsFeatureExtractor(),
                new CountOfFieldsFeatureExtractor(),
                new CountOfObjectFieldsFeatureExtractor(),
                new CountOfOtherClassesWithOwnFieldTypeFeatureExtractor(),


                //Constructor Level Metrics
                new CountOfReferenceAsConstructorParameter(),
                new CountOfPrivateConstructorsFeatureExtractor(),

                //Method Level Metrics
                new CountOfAbstractMethodsFeatureExtractor(),
                new CountOfPrivateMethodsFeatureExtractor(),
                new CountOfReferenceAsMethodParameterFeatureExtractor(),
                new CountOfReferenceAsReturnTypeFeatureExtractor(),
                new CountOfReferenceAsVariableFeatureExtractor(),
                new CountOfStaticMethodsFeatureExtractor(),
                new CountOfMethodsFeatureExtractor(),

                new CountOfOverriddenMethodsFeatureExtractor(),

        };
    }

    private ClassMetricVector[] extract(SourceFileStreamer.Element el) {
        var processedVectors = new LinkedList<ClassMetricVector>();
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
            processedVectors.add(vector);
        }

        var finishedVectors = processedVectors.toArray(ClassMetricVector[]::new);
        finishedVectors = new CkFeatureExtractor(projectsPath).calculateMetrics(finishedVectors);
        return finishedVectors;
    }

    public Stream<ClassMetricVector> extractFeatures(Stream<SourceFileStreamer.Element> elementStream) {
        return elementStream
                .map(this::extract)
                .flatMap(Arrays::stream);

    }

}
