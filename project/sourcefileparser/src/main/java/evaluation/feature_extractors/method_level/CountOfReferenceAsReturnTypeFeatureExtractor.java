package evaluation.feature_extractors.method_level;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.Type;
import evaluation.ClassMetricVector;
import evaluation.ClassMetricVectorConstants;
import evaluation.feature_extractors.BaseFeatureExtractor;

import java.util.List;
import java.util.Map;

public class CountOfReferenceAsReturnTypeFeatureExtractor extends BaseFeatureExtractor {
    @Override
    public String getFeatureName() {
        return ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_RETURN_TYPE;
    }

    @Override
    public int extract(ClassOrInterfaceDeclaration currentClassOrInterface, Map<String, ClassOrInterfaceDeclaration> participants, ClassMetricVector[] foundClassVectors) {
        var entityName = currentClassOrInterface.getNameAsString();
        return (int)participants.values()
                .stream()
                .map(ClassOrInterfaceDeclaration::getMethods)
                .flatMap(List::stream)
                .map(MethodDeclaration::getType)
                .map(Type::asString)
                .filter(entityName::contentEquals)
                .count();

    }
}
