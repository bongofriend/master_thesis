package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.MetricGatherer;

import java.util.Set;

public class NumberOfOverriddenMethodsEvaluation extends BaseMetricEvaluation {
    @Override
    public String getMetricName() {
        return "NORM";
    }

    //TODO
    @Override
    public int evaluate(ClassOrInterfaceDeclaration unit, MetricGatherer metricGatherer, Set<String> microArchitectureParticipants) {
        return 0;
    }
}
