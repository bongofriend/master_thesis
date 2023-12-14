package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.MetricGatherer;

import java.util.Set;

public class NumberOfOtherClassesWithFieldOfOwnTypeEvaluation extends BaseMetricEvaluation {
    @Override
    public String getMetricName() {
        return "NCOF";
    }

    @Override
    public int evaluate(ClassOrInterfaceDeclaration unit, MetricGatherer metricGatherer, Set<String> microStructureEntities) {
        return 0;
    }
}
