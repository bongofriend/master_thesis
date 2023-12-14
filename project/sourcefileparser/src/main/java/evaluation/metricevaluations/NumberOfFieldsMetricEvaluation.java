package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.MetricGatherer;

import java.util.Set;

public class NumberOfFieldsMetricEvaluation extends BaseMetricEvaluation {
    @Override
    public String getMetricName() {
        return "NOF";
    }

    @Override
    public int evaluate(ClassOrInterfaceDeclaration declaration, MetricGatherer metricGatherer, Set<String> microArchitectureParticipants) {
        var count = declaration.getFields().size();
        return count;
    }
}
