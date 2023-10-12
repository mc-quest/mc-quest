package net.mcquest.server.constants;

import net.mcquest.server.Assets;
import net.mcquest.core.asset.Asset;
import net.mcquest.core.particle.ParticleEffect;

public class ParticleEffects {
    public static final ParticleEffect RAINBOW = loadParticleEffect("rainbow");

    private static ParticleEffect loadParticleEffect(String fileName) {
        Asset description = Assets.asset("particleeffects/" + fileName + ".particle.json");
        return new ParticleEffect(description);
    }
}
