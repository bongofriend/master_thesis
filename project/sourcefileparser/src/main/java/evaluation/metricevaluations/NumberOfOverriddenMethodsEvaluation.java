package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import evaluation.Parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class NumberOfOverriddenMethodsEvaluation implements MetricEvaluation {
    @Override
    public String getMetricName() {
        return "NORM";
    }

    //TODO
    @Override
    public int evaluate(ClassOrInterfaceDeclaration unit, Parser metricGatherer, Set<String> microArchitectureParticipants) {
        var implementedOrExtended = new LinkedList<>(unit.getExtendedTypes());
        implementedOrExtended.addAll(unit.getImplementedTypes());
        if (implementedOrExtended.isEmpty()) {
            return 0;
        }
        var overriddenMethodCounter = 0;
        var entityToNameMap = new HashMap<String, String>();
        var classMethodDeclarations = unit.findAll(MethodDeclaration.class);
        microArchitectureParticipants
                .forEach(s -> {
                    var segments = s.split("\\.");
                    entityToNameMap.put(segments[segments.length - 1], s);
                });
        for (var e : implementedOrExtended) {
            if (!entityToNameMap.containsKey(e.getNameAsString())) {
                continue;
            }
            var classOrInterfaceDeclaration = metricGatherer.getCompilationUnit(entityToNameMap.get(e.getNameAsString()));
            if (classOrInterfaceDeclaration == null) {
                continue;
            }
            var methodsDeclarations = classOrInterfaceDeclaration.findAll(MethodDeclaration.class);
            for (var mEx : methodsDeclarations) {
                var returnTypeExtended = mEx.getType();
                var nameExtended = mEx.getNameAsString();
                var parametersTypesExtended = mEx.getParameters();
                for (var mCls : classMethodDeclarations) {
                    var returnTypeCls = mCls.getType();
                    var nameCls = mCls.getNameAsString();
                    var parametersCls = mCls.getParameters();

                    if (returnTypeCls.equals(returnTypeExtended) && nameCls.equals(nameExtended) && new HashSet<>(parametersCls).containsAll(parametersTypesExtended)) {
                        overriddenMethodCounter++;
                    }
                }
            }

        }
        return overriddenMethodCounter;
    }
}
