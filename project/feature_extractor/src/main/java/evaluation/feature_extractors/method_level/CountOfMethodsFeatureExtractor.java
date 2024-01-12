package evaluation.feature_extractors.method_level;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.ClassMetricVector;
import evaluation.ClassMetricVectorConstants;
import evaluation.feature_extractors.BaseFeatureExtractor;

import java.util.Map;

public class CountOfMethodsFeatureExtractor extends BaseFeatureExtractor {
    @Override
    public String getFeatureName() {
        return ClassMetricVectorConstants.COUNT_OF_METHODS;
    }

    @Override
    public int extract(ClassOrInterfaceDeclaration currentClassOrInterface, Map<String, ClassOrInterfaceDeclaration> participants, ClassMetricVector[] foundClassVectors, Map<String, ClassOrInterfaceDeclaration> allClassDeclarations) {
        return currentClassOrInterface
                .getMethods()
                .size();
    }
}
