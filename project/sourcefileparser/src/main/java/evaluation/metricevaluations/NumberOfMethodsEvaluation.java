package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import evaluation.MetricGatherer;

import java.util.Set;

public class NumberOfMethodsEvaluation extends BaseMetricEvaluation{
    @Override
    public String getMetricName() {
        return "NOM";
    }

    @Override
    public int evaluate(ClassOrInterfaceDeclaration unit, MetricGatherer metricGatherer, Set<String> microArchitectureParticipants) {
        return unit
                .findAll(MethodDeclaration.class)
                .size();
    }
}
