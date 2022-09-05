package com.mcquest.server.feature;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FeatureManager {
    private final Map<Class<? extends Feature>, Feature> features;

    @ApiStatus.Internal
    public FeatureManager() {
        features = new HashMap();
    }

    public void addFeature(Feature feature) {
        if (features.containsKey(feature.getClass())) {
            throw new IllegalArgumentException("Feature already added: " + feature.getClass().getName());
        }
        features.put(feature.getClass(), feature);
    }

    public <T extends Feature> T getFeature(Class<T> featureClass) {
        return (T) features.get(featureClass);
    }

    public Collection<Feature> getFeatures() {
        return features.values();
    }
}
