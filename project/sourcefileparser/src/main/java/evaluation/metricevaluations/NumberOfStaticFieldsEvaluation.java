package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import evaluation.MetricGatherer;

import java.util.Set;

public class NumberOfStaticFieldsEvaluation extends BaseMetricEvaluation{
    @Override
    public String getMetricName() {
        return "NOF";
    }

    @Override
    public int evaluate(ClassOrInterfaceDeclaration unit, MetricGatherer metricGatherer, Set<String> microArchitectureParticipants) {
        return ((int) unit
                .getFields()
                .stream()
                .filter(FieldDeclaration::isStatic)
                .count());
    }
}
