package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import evaluation.Parser;

import java.util.Set;

public class NumberOfPrivateConstructorsEvaluation implements MetricEvaluation {
    @Override
    public String getMetricName() {
        return "NOPC";
    }

    @Override
    public int evaluate(ClassOrInterfaceDeclaration unit, Parser metricGatherer, Set<String> microArchitectureParticipants) {
        return (int) unit
                .findAll(ConstructorDeclaration.class)
                .stream()
                .filter(ConstructorDeclaration::isPrivate)
                .count();
    }
}
