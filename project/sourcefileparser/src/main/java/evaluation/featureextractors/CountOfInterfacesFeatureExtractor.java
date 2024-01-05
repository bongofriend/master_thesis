package evaluation.featureextractors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.ClassMetricVectorConstants;

import java.util.Map;

public class CountOfInterfacesFeatureExtractor implements FeatureExtractor {
    @Override
    public String getFeatureName() {
        return ClassMetricVectorConstants.COUNT_OF_INTERFACES;
    }

    @Override
    public int extract(ClassOrInterfaceDeclaration currentClassOrInterface, Map<String, ClassOrInterfaceDeclaration> participants) {
        return currentClassOrInterface.getImplementedTypes().size();
    }
}
