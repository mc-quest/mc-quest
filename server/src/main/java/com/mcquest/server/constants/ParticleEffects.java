package com.mcquest.server.constants;

import com.mcquest.server.Assets;
import com.mcquest.server.asset.Asset;
import com.mcquest.server.particle.ParticleEffect;

public class ParticleEffects {
    public static final ParticleEffect RAINBOW = loadParticleEffect("rainbow");

    private static ParticleEffect loadParticleEffect(String fileName) {
        Asset description = Assets.asset("particleeffects/" + fileName + ".particle.json");
        return new ParticleEffect(description);
    }
}
