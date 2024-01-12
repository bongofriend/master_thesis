package evaluation.feature_extractors.class_level;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.ClassMetricVector;
import evaluation.ClassMetricVectorConstants;
import evaluation.feature_extractors.BaseFeatureExtractor;

import java.util.Map;

@Deprecated
public class ExtendsEntityFeatureExtractor extends BaseFeatureExtractor {
    @Override
    public String getFeatureName() {
        return ClassMetricVectorConstants.EXTENDS_ENTITY;
    }

    @Override
    public int extract(ClassOrInterfaceDeclaration currentClassOrInterface, Map<String, ClassOrInterfaceDeclaration> participants, ClassMetricVector[] foundClassVectors, Map<String, ClassOrInterfaceDeclaration> allClassDeclarations) {
        return currentClassOrInterface.getExtendedTypes().size();
    }
}
