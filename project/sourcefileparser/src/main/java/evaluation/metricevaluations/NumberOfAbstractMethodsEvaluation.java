package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import evaluation.MetricGatherer;

import java.util.Set;

public class NumberOfAbstractMethodsEvaluation extends BaseMetricEvaluation {
    @Override
    public String getMetricName() {
        return "NOAM";
    }

    @Override
    public int evaluate(ClassOrInterfaceDeclaration unit, MetricGatherer metricGatherer, Set<String> microArchitectureParticipants) {
        return (int) unit
                .findAll(MethodDeclaration.class)
                .stream()
                .filter(MethodDeclaration::isAbstract)
                .count();
    }
}
