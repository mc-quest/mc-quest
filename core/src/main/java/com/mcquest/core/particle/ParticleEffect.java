package com.mcquest.core.particle;

import com.mcquest.core.asset.Asset;
import com.mcquest.core.asset.AssetTypes;

public class ParticleEffect {
    private final Asset snowstorm;

    public ParticleEffect(Asset snowstorm) {
        snowstorm.requireType(AssetTypes.JSON);
        this.snowstorm = snowstorm;
    }

    public Asset getSnowstorm() {
        return snowstorm;
    }
}
