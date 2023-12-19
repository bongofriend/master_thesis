package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.Parser;

import java.util.Set;

public class NumberOfFieldsMetricEvaluation implements MetricEvaluation {
    @Override
    public String getMetricName() {
        return "NOF";
    }

    @Override
    public int evaluate(ClassOrInterfaceDeclaration declaration, Parser metricGatherer, Set<String> microArchitectureParticipants) {
        return declaration.getFields().size();
    }
}
