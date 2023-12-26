package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import evaluation.MetricEvaluationResultConstants;
import evaluation.Parser;

import java.util.Set;

public class NumberOfStaticMethodsEvaluation implements MetricEvaluation {

    @Override
    public String getMetricName() {
        return MetricEvaluationResultConstants.NSM;
    }

    @Override
    public int evaluate(ClassOrInterfaceDeclaration unit, Parser metricGatherer, Set<String> microArchitectureParticipants) {
        return (int) unit
                .findAll(MethodDeclaration.class)
                .stream()
                .filter(MethodDeclaration::isStatic)
                .count();
    }
}
