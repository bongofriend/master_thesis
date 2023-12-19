package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import evaluation.Parser;

import java.util.Set;

public class NumberOfObjectFieldsEvaluation implements MetricEvaluation {
    @Override
    public String getMetricName() {
        return "NOOF";
    }

    @Override
    public int evaluate(ClassOrInterfaceDeclaration unit, Parser metricGatherer, Set<String> microArchitectureParticipants) {
        return (int) unit
                .findAll(FieldDeclaration.class)
                .stream()
                .filter(f -> f.getCommonType().isReferenceType())
                .count();
    }
}
