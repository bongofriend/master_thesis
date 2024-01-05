package evaluation.feature_extractors.construtor_level;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import evaluation.ClassMetricVector;
import evaluation.ClassMetricVectorConstants;
import evaluation.feature_extractors.BaseFeatureExtractor;
import evaluation.feature_extractors.FeatureExtractor;

import java.util.Map;

public class CountOfReferenceAsConstructorParameter extends BaseFeatureExtractor {
    @Override
    public String getFeatureName() {
        return ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_CONSTRUCTOR_PARAMETER;
    }

    @Override
    public int extract(ClassOrInterfaceDeclaration currentClassOrInterface, Map<String, ClassOrInterfaceDeclaration> participants, ClassMetricVector[] foundClassVectors) {
        var entityName = currentClassOrInterface.getNameAsString();
        for(var k: participants.keySet()) {
            var simpleName = getEntityName(k);
            if (simpleName.contentEquals(entityName)) {
                continue;
            }
            var d = participants.get(k).getConstructors();
            return (int)d
                    .stream()
                    .map(ConstructorDeclaration::getParameters)
                    .flatMap(NodeList::stream)
                    .filter(p -> p.getType().isReferenceType())
                    .filter(p -> {
                        var typeName = p.getType().asString();
                        return typeName.contentEquals(entityName);
                    })
                    .count();
        }
        return 0;
    }
}
