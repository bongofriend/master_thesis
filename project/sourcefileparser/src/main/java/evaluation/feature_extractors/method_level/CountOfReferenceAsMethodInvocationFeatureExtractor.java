package evaluation.feature_extractors.method_level;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import evaluation.ClassMetricVector;
import evaluation.ClassMetricVectorConstants;
import evaluation.feature_extractors.BaseFeatureExtractor;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Deprecated
public class CountOfReferenceAsMethodInvocationFeatureExtractor extends BaseFeatureExtractor {
    @Override
    public String getFeatureName() {
        return ClassMetricVectorConstants.COUNT_OF_REFERENCE_AS_METHOD_INVOCATION;
    }

    @Override
    public int extract(ClassOrInterfaceDeclaration currentClassOrInterface, Map<String, ClassOrInterfaceDeclaration> participants, ClassMetricVector[] foundClassVectors, Map<String, ClassOrInterfaceDeclaration> allClassDeclarations) {
        var entityName = currentClassOrInterface.getNameAsString();
        var count = participants.values()
                .stream()
                .map(ClassOrInterfaceDeclaration::getMethods)
                .flatMap(List::stream)
                .map(m -> {
                    if(m.getBody().isPresent()) {
                        return m.getBody().get().getStatements();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(s -> {
                    if(!s.isExpressionStmt()) {
                        return false;
                    }
                    var ex = s.asExpressionStmt().getExpression();
                    if(!ex.isMethodCallExpr()) {
                        return false;
                    }
                    var mEx = ex.asMethodCallExpr();
                    return isMethodCallOnVariableType(mEx, entityName);
                })
                .count();
        return (int) count;
    }

    private boolean isMethodCallOnVariableType(MethodCallExpr mEx, String entity) {
        return mEx
                .getScope()
                .filter(Expression::isNameExpr)
                .map(Expression::asNameExpr)
                .map(nEx -> {
                    try {
                        return nEx.calculateResolvedType().describe().contentEquals(entity);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .orElse(false);
    }
}
