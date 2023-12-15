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
        var ownName = unit.getNameAsString();
        var counter = 0;

        for (var c: microStructureEntities) {
            var d = metricGatherer.getCompilationUnit(c);
            if(d == null) {
                continue;
            }
            for (var f: d.getFields()) {
                var fieldTypeName = f.getCommonType().asString();
                if (fieldTypeName.equals(ownName)) {
                    counter++;
                }
            }
        }
        return counter;
    }
}
