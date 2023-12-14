package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.MetricGatherer;

import java.util.Set;

public class NumberOfInterfacesEvaluation extends BaseMetricEvaluation {

    @Override
    public String getMetricName() {
        return "NOI";
    }

    @Override
    public int evaluate(ClassOrInterfaceDeclaration unit, MetricGatherer metricGatherer, Set<String> microArchitectureParticipants) {
        return unit.getImplementedTypes().size();
    }
}
