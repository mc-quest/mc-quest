package net.mcquest.core.particle;

import net.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.EntityTracker;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.particle.ParticleCreator;

public class ParticleEffects {
    public static void wireframeBox(Instance instance, Pos min, Pos max,
                                    Particle particle, double density) {
        Pos minMinMin = min;
        Pos minMinMax = min.withZ(max.z());
        Pos minMaxMin = min.withY(max.y());
        Pos minMaxMax = max.withX(min.x());
        Pos maxMinMin = min.withX(max.x());
        Pos maxMinMax = max.withY(min.y());
        Pos maxMaxMin = max.withZ(min.z());
        Pos maxMaxMax = max;

        // Bottom face.
        line(instance, minMinMin, minMinMax, particle, density);
        line(instance, minMinMax, maxMinMax, particle, density);
        line(instance, maxMinMax, maxMinMin, particle, density);
        line(instance, maxMinMin, minMinMin, particle, density);

        // Top face.
        line(instance, minMaxMin, minMaxMax, particle, density);
        line(instance, minMaxMax, maxMaxMax, particle, density);
        line(instance, maxMaxMax, maxMaxMin, particle, density);
        line(instance, maxMaxMin, minMaxMin, particle, density);

        // Vertical edges.
        line(instance, minMinMin, minMaxMin, particle, density);
        line(instance, minMinMax, minMaxMax, particle, density);
        line(instance, maxMinMin, maxMaxMin, particle, density);
        line(instance, maxMinMax, maxMaxMax, particle, density);
    }

    public static void wireframeBox(Instance instance, Pos center, Vec extents,
                                    Particle particle, double density) {
        Vec halfExtents = extents.mul(0.5);
        wireframeBox(
                instance,
                center.sub(halfExtents),
                center.add(halfExtents),
                particle,
                density
        );
    }

    public static void line(Instance instance, Pos start, Vec direction,
                            double length, Particle particle, double density) {
        if (!direction.isNormalized()) {
            direction = direction.normalize();
        }
        int particleCount = (int) (length * density);
        Pos particlePosition = start;
        double space = 1.0 / density;
        Vec increment = direction.mul(space);
        for (int i = 0; i < particleCount; i++) {
            particle(instance, particlePosition, particle);
            particlePosition = particlePosition.add(increment);
        }
    }

    public static void line(Instance instance, Pos start, Pos end, Particle particle, double density) {
        Vec v = end.sub(start).asVec();
        double length = v.length();
        Vec direction = v.normalize();
        line(instance, start, direction, length, particle, density);
    }

    public static void particle(Instance instance, Pos position, Particle particle) {
        instance.getEntityTracker().nearbyEntities(position, 50.0, EntityTracker.Target.PLAYERS, player -> {
            ParticlePacket packet = ParticleCreator.createParticlePacket(
                    particle, position.x(), position.y(), position.z(),
                    0.0f, 0.0f, 0.0f, 1);
            player.sendPacket(packet);
        });
    }
}
