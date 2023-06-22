package com.mcquest.core.particle;

import com.mcquest.core.asset.Asset;

public class ParticleEffect {
    private final Asset snowstorm;

    public ParticleEffect(Asset snowstorm) {
        snowstorm.requireType("json");
        this.snowstorm = snowstorm;
    }

    public Asset getSnowstorm() {
        return snowstorm;
    }
}
