package evaluation.feature_extractors.method_level;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.nodeTypes.NodeWithType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import evaluation.ClassMetricVector;
import evaluation.ClassMetricVectorConstants;
import evaluation.feature_extractors.BaseFeatureExtractor;

import java.util.*;
import java.util.stream.Collectors;

public class CountOfOverriddenMethodsFeatureExtractor extends BaseFeatureExtractor {

    private record MethodSignature(String returnType, String methodName, String[] parameterTypes) {
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            var m = (MethodSignature) obj;
            return returnType.contentEquals(m.returnType) && methodName.contentEquals(m.methodName) && Arrays.equals(parameterTypes, m.parameterTypes);

        }
    }

    private MethodSignature toMethodSignature(MethodDeclaration methodDeclaration) {
        var returnType = methodDeclaration.getType();
        var methodName = methodDeclaration.getNameAsString();
        var parameterTypes = methodDeclaration
                .getParameters()
                .stream()
                .map(NodeWithType::getTypeAsString)
                .toArray(String[]::new);
        return new MethodSignature(returnType.asString(), methodName, parameterTypes);
    }

    private List<MethodSignature> getMethodSignatures(ClassOrInterfaceDeclaration declaration) {
        return declaration
                .getMethods()
                .stream()
                .map(this::toMethodSignature)
                .toList();
    }

    private Map<String, ClassOrInterfaceDeclaration> transformKeys(Map<String, ClassOrInterfaceDeclaration> participants) {
        var transformed = new HashMap<String, ClassOrInterfaceDeclaration>();
        participants.keySet()
                .forEach(e -> {
                    var entity = getEntityName(e);
                    if(entity.isEmpty()) {
                        return;
                    }
                    transformed.put(entity.get(), participants.get(e));
                });
        return transformed;
    }

    @Override
    public String getFeatureName() {
        return ClassMetricVectorConstants.COUNT_OF_OVERRIDDEN_METHODS;
    }

    @Override
    public int extract(ClassOrInterfaceDeclaration currentClassOrInterface, Map<String, ClassOrInterfaceDeclaration> participants, ClassMetricVector[] foundClassVectors, Map<String, ClassOrInterfaceDeclaration> allClassDeclarations) {
        if(currentClassOrInterface.getExtendedTypes().isEmpty()) {
            return 0;
        }

        var ownMethods = getMethodSignatures(currentClassOrInterface);
        var resolvedParticipants = transformKeys(participants);

        var count = currentClassOrInterface.getExtendedTypes()
                .stream()
                .map(ClassOrInterfaceType::getNameAsString)
                .filter(resolvedParticipants::containsKey)
                .map(resolvedParticipants::get)
                .map(ClassOrInterfaceDeclaration::getMethods)
                .flatMap(List::stream)
                .map(this::toMethodSignature)
                .filter(ownMethods::contains)
                .count();
        return (int) count;
    }
}
