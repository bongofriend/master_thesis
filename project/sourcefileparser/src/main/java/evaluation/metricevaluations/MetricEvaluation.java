package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.Parser;

import java.util.Set;

public interface MetricEvaluation {
    String getMetricName();
    int evaluate(ClassOrInterfaceDeclaration unit, Parser metricGatherer, Set<String> microArchitectureParticipants);
}
