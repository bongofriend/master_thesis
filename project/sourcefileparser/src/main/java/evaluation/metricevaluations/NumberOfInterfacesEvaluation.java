package evaluation.metricevaluations;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import evaluation.Parser;

import java.util.Set;

public class NumberOfInterfacesEvaluation implements MetricEvaluation {

    @Override
    public String getMetricName() {
        return "NOI";
    }

    @Override
    public int evaluate(ClassOrInterfaceDeclaration unit, Parser metricGatherer, Set<String> microArchitectureParticipants) {
        return unit.getImplementedTypes().size();
    }
}
