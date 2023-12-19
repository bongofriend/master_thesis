package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import evaluation.Parser;

import java.util.Set;

public class NumberOfAbstractMethodsEvaluation implements MetricEvaluation {
    @Override
    public String getMetricName() {
        return "NOAM";
    }

    @Override
    public int evaluate(ClassOrInterfaceDeclaration unit, Parser metricGatherer, Set<String> microArchitectureParticipants) {
        return (int) unit
                .findAll(MethodDeclaration.class)
                .stream()
                .filter(MethodDeclaration::isAbstract)
                .count();
    }
}
