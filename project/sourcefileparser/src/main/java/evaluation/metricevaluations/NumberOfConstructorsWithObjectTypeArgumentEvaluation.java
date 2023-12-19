package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import evaluation.Parser;

import java.util.Set;

public class NumberOfConstructorsWithObjectTypeArgumentEvaluation implements MetricEvaluation {
    @Override
    public String getMetricName() {
        return "NOTC";
    }

    @Override
    public int evaluate(ClassOrInterfaceDeclaration unit, Parser metricGatherer, Set<String> microArchitectureParticipants) {
        return (int) unit
                .findAll(ConstructorDeclaration.class)
                .stream()
                .filter(this::hasObjectTypeAsParameter)
                .count();
    }

    private boolean hasObjectTypeAsParameter(ConstructorDeclaration c) {
        for (var p : c.getParameters()) {
            if (p.getType().isReferenceType()) {
                return true;
            }
        }
        return false;
    }
}
