package evaluation.feature_extractors.field_level;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import evaluation.ClassMetricVector;
import evaluation.ClassMetricVectorConstants;
import evaluation.feature_extractors.BaseFeatureExtractor;
import evaluation.feature_extractors.FeatureExtractor;

import java.util.Map;

public class CountOfPrivateFieldsFeatureExtractor extends BaseFeatureExtractor {
    @Override
    public String getFeatureName() {
        return ClassMetricVectorConstants.COUNT_OF_PRIVATE_FIELDS;
    }

    @Override
    public int extract(ClassOrInterfaceDeclaration currentClassOrInterface, Map<String, ClassOrInterfaceDeclaration> participants, ClassMetricVector[] foundClassVectors) {
        return (int) currentClassOrInterface
                .getFields()
                .stream()
                .filter(FieldDeclaration::isPrivate)
                .count();
    }
}
