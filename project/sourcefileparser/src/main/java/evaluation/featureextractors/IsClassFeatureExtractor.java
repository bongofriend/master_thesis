package evaluation.featureextractors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.ClassMetricVectorConstants;

import java.util.Map;

public class IsClassFeatureExtractor implements FeatureExtractor {
    @Override
    public String getFeatureName() {
        return ClassMetricVectorConstants.IS_CLASS;
    }

    @Override
    public int extract(ClassOrInterfaceDeclaration currentClassOrInterface, Map<String, ClassOrInterfaceDeclaration> participants) {
        if(!currentClassOrInterface.isInterface()) {
            return 1;
        }
        return 0;
    }
}
