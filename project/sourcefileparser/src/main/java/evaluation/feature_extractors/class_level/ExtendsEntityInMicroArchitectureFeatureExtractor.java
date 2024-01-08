package evaluation.feature_extractors.class_level;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.ClassMetricVector;
import evaluation.ClassMetricVectorConstants;
import evaluation.feature_extractors.BaseFeatureExtractor;

import java.util.Map;

public class ExtendsEntityInMicroArchitectureFeatureExtractor extends BaseFeatureExtractor {
    @Override
    public String getFeatureName() {
        return ClassMetricVectorConstants.EXTENDS_ENTITY_IN_MICRO_ARCHITECTURE;
    }

    @Override
    public int extract(ClassOrInterfaceDeclaration currentClassOrInterface, Map<String, ClassOrInterfaceDeclaration> participants, ClassMetricVector[] foundClassMetricVectors, Map<String, ClassOrInterfaceDeclaration> allClassDeclarations) {
        var extendedTypes = currentClassOrInterface.getExtendedTypes();
        if(extendedTypes.isEmpty()) {
            return 0;
        }
        for(var v: foundClassMetricVectors) {
            if(currentClassOrInterface.getFullyQualifiedName().isEmpty()) {
                continue;
            }
            if(v.getEntity().contentEquals(currentClassOrInterface.getFullyQualifiedName().get())) {
                continue;
            }
            var entityName = getEntityName(v);
            for(var e: extendedTypes) {
                var name = e.getNameAsString();
                if(entityName.isPresent() && name.contentEquals(entityName.get())) {
                    return 1;
                }
            }
        }
        return 0;
    }
}
