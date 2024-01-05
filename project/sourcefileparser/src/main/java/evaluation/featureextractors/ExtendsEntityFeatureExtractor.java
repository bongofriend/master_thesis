package evaluation.featureextractors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.ClassMetricVectorConstants;

import java.util.Map;

public class ExtendsEntityFeatureExtractor implements FeatureExtractor {
    @Override
    public String getFeatureName() {
        return ClassMetricVectorConstants.EXTENDS_ENTITY;
    }

    @Override
    public int extract(ClassOrInterfaceDeclaration currentClassOrInterface, Map<String, ClassOrInterfaceDeclaration> participants) {
        return currentClassOrInterface.getExtendedTypes().size();
    }
}
