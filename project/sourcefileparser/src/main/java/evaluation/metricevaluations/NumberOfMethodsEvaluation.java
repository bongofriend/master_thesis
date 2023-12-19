package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import evaluation.Parser;

import java.util.Set;

public class NumberOfMethodsEvaluation implements MetricEvaluation {
    @Override
    public String getMetricName() {
        return "NOM";
    }

    @Override
    public int evaluate(ClassOrInterfaceDeclaration unit, Parser metricGatherer, Set<String> microArchitectureParticipants) {
        return unit
                .findAll(MethodDeclaration.class)
                .size();
    }
}
