package evaluation.feature_extractors;

import evaluation.ClassMetricVector;

import java.util.Arrays;

public abstract class BaseFeatureExtractor implements FeatureExtractor {
    protected String getEntityName(ClassMetricVector vector) {
        return getEntityName(vector.getEntity());

    }

    protected String getEntityName(String entity) {
        return Arrays.stream(entity.split("\\."))
                .filter(s -> !s.isEmpty() && Character.isUpperCase(s.charAt(0)))
                .reduce((a, b) -> b)
                .get();

    }
}
