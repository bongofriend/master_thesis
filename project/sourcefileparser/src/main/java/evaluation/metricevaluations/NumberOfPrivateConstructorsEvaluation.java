package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import evaluation.MetricGatherer;

import java.util.Set;

public class NumberOfPrivateConstructorsEvaluation extends BaseMetricEvaluation{
    @Override
    public String getMetricName() {
        return "NOPC";
    }

    @Override
    public int evaluate(ClassOrInterfaceDeclaration unit, MetricGatherer metricGatherer, Set<String> microArchitectureParticipants) {
        return (int) unit
                .findAll(ConstructorDeclaration.class)
                .stream()
                .filter(ConstructorDeclaration::isPrivate)
                .count();
    }
}
