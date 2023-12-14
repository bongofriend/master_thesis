package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.MetricGatherer;

import java.util.Set;

public interface MetricEvaluation {
    String getMetricName();
    int evaluate(ClassOrInterfaceDeclaration unit, MetricGatherer metricGatherer, Set<String> microArchitectureParticipants);
}
