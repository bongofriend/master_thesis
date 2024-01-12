package evaluation.feature_extractors.construtor_level;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import evaluation.ClassMetricVector;
import evaluation.ClassMetricVectorConstants;
import evaluation.feature_extractors.BaseFeatureExtractor;

import java.util.Map;

public class CountOfPrivateConstructorsFeatureExtractor extends BaseFeatureExtractor {
    @Override
    public String getFeatureName() {
        return ClassMetricVectorConstants.COUNT_OF_PRIVATE_CONSTRUCTORS;
    }

    @Override
    public int extract(ClassOrInterfaceDeclaration currentClassOrInterface, Map<String, ClassOrInterfaceDeclaration> participants, ClassMetricVector[] foundClassVectors, Map<String, ClassOrInterfaceDeclaration> allClassDeclarations) {
        return (int) currentClassOrInterface
                .getConstructors()
                .stream()
                .filter(ConstructorDeclaration::isPrivate)
                .count();
    }
}
