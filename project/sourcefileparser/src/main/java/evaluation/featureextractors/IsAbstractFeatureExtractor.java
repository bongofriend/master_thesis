package evaluation.featureextractors;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.ClassMetricVector;
import evaluation.ClassMetricVectorConstants;

import java.util.Map;

public class IsAbstractFeatureExtractor implements FeatureExtractor {
    @Override
    public String getFeatureName() {
        return ClassMetricVectorConstants.IS_ABSTRACT;
    }

    @Override
    public int extract(ClassOrInterfaceDeclaration currentClassOrInterface, Map<String, ClassOrInterfaceDeclaration> participants) {
        if(currentClassOrInterface.isAbstract()) {
            return 1;
        }
        return 0;
    }
}
