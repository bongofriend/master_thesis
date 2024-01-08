package evaluation.feature_extractors.field_level;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.ClassMetricVector;
import evaluation.ClassMetricVectorConstants;
import evaluation.feature_extractors.BaseFeatureExtractor;

import java.util.List;
import java.util.Map;

public class CountOfOtherClassesWithOwnFieldTypeFeatureExtractor extends BaseFeatureExtractor {
    @Override
    public String getFeatureName() {
        return ClassMetricVectorConstants.COUNT_OF_OTHER_CLASSES_WITH_FIELD_OF_OWN_TYPE;
    }

    @Override
    public int extract(ClassOrInterfaceDeclaration currentClassOrInterface, Map<String, ClassOrInterfaceDeclaration> participants, ClassMetricVector[] foundClassVectors, Map<String, ClassOrInterfaceDeclaration> allClassDeclarations) {
        var ownName = currentClassOrInterface.getNameAsString();
        return (int) participants.values()
                .stream()
                .map(ClassOrInterfaceDeclaration::getFields)
                .flatMap(List::stream)
                .filter(f -> f.getCommonType().asString().contentEquals(ownName))
                .count();
    }
}
