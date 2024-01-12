package evaluation.feature_extractors.field_level;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithVariables;
import evaluation.ClassMetricVector;
import evaluation.ClassMetricVectorConstants;
import evaluation.feature_extractors.BaseFeatureExtractor;

import java.util.Map;

@Deprecated
public class CountOfReferenceInFieldsFeatureExtractor extends BaseFeatureExtractor {
    @Override
    public String getFeatureName() {
        return ClassMetricVectorConstants.COUNT_OF_REFERENCE_IN_FIELDS;
    }

    @Override
    public int extract(ClassOrInterfaceDeclaration currentClassOrInterface, Map<String, ClassOrInterfaceDeclaration> participants, ClassMetricVector[] foundClassVectors, Map<String, ClassOrInterfaceDeclaration> allClassDeclarations) {
        var entityName = currentClassOrInterface.getNameAsString();
        for (var k : participants.keySet()) {
            var d = participants.get(k).getFields();
            return (int) d
                    .stream()
                    .map(NodeWithVariables::getCommonType)
                    .filter(p -> {
                        var typeName = p.asString();
                        return typeName.contentEquals(entityName);
                    })
                    .count();
        }
        return 0;
    }
}
