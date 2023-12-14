package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import evaluation.MetricGatherer;

import java.util.Set;

public class NumberOfStaticMethodsEvaluation extends BaseMetricEvaluation {

    @Override
    public String getMetricName() {
        return "NSM";
    }

    @Override
    public int evaluate(ClassOrInterfaceDeclaration unit, MetricGatherer metricGatherer, Set<String> microArchitectureParticipants) {
        return (int) unit
                .findAll(MethodDeclaration.class)
                .stream()
                .filter(MethodDeclaration::isStatic)
                .count();
    }
}
