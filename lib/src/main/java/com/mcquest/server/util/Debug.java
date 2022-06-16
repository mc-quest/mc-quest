package com.mcquest.server.util;

import com.mcquest.server.physics.Collider;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.instance.Instance;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Debug {
    private static final Set<Collider> shownColliders = new HashSet<>();

    static {
        SchedulerManager schedulerManager =
                MinecraftServer.getSchedulerManager();
        schedulerManager.submitTask(() -> {
            for (Collider collider : shownColliders) {
                drawCollider(collider);
            }
            return TaskSchedule.millis(100);
        });
    }

    private static void drawCollider(Collider collider) {
        Instance instance = collider.getInstance();
        Pos center = collider.getCenter();
        Collection<Entity> nearbyEntities =
                instance.getNearbyEntities(center, 50.0);
        ParticleEffects.wireframeBox(instance, collider.getMin(),
                collider.getMax(), Particle.CRIT, 4.0);
    }

    public static void showCollider(Collider collider) {
        shownColliders.add(collider);
    }

    public static void hideCollider(Collider collider) {
        shownColliders.remove(collider);
    }
}
