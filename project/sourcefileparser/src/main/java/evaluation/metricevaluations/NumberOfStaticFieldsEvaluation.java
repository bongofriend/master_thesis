package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import evaluation.Parser;

import java.util.Set;

public class NumberOfStaticFieldsEvaluation implements MetricEvaluation {
    @Override
    public String getMetricName() {
        return "NSF";
    }

    @Override
    public int evaluate(ClassOrInterfaceDeclaration unit, Parser metricGatherer, Set<String> microArchitectureParticipants) {
        return ((int) unit
                .getFields()
                .stream()
                .filter(FieldDeclaration::isStatic)
                .count());
    }
}
