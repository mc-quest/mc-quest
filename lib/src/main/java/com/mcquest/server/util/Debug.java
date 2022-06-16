package com.mcquest.server.util;

import com.mcquest.server.physics.Collider;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.particle.Particle;
import net.minestom.server.particle.ParticleCreator;

import java.util.Set;

public class Debug {
    private static Set<Collider> shownColliders;

    public static void showCollider(Collider collider) {
        if (shownColliders.contains(collider)) {
            throw new IllegalArgumentException("collider already shown");
        }
        shownColliders.add(collider);
        // TODO
    }

    public static void hideCollider(Collider collider) {
        if (!shownColliders.contains(collider)) {
            throw new IllegalArgumentException("collider already hidden");
        }
        shownColliders.remove(collider);
    }
}
