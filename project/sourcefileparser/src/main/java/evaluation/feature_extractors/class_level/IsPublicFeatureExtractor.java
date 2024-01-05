package evaluation.feature_extractors.class_level;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.ClassMetricVector;
import evaluation.ClassMetricVectorConstants;
import evaluation.feature_extractors.BaseFeatureExtractor;
import evaluation.feature_extractors.FeatureExtractor;

import java.util.Map;

public class IsPublicFeatureExtractor extends BaseFeatureExtractor {

    @Override
    public String getFeatureName() {
        return ClassMetricVectorConstants.IS_PUBLIC;
    }

    @Override
    public int extract(ClassOrInterfaceDeclaration currentClassOrInterface, Map<String, ClassOrInterfaceDeclaration> participants, ClassMetricVector[] foundClassVectors) {
        if(currentClassOrInterface.isPublic()) {
            return 1;
        }
        return 0;
    }
}
