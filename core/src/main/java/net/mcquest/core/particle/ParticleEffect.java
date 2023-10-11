package net.mcquest.core.particle;

import net.mcquest.core.asset.Asset;
import net.mcquest.core.asset.AssetTypes;

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
