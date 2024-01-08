package evaluation.feature_extractors.class_level;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.ClassMetricVector;
import evaluation.ClassMetricVectorConstants;
import evaluation.feature_extractors.BaseFeatureExtractor;

import java.util.Map;

public class IsStaticFeatureExtractor extends BaseFeatureExtractor {
    @Override
    public String getFeatureName() {
        return ClassMetricVectorConstants.IS_STATIC;
    }

    @Override
    public int extract(ClassOrInterfaceDeclaration currentClassOrInterface, Map<String, ClassOrInterfaceDeclaration> participants, ClassMetricVector[] foundClassVectors, Map<String, ClassOrInterfaceDeclaration> allClassDeclarations) {
        if(currentClassOrInterface.isStatic()) {
            return 1;
        }
        return 0;
    }
}
