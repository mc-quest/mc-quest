package com.mcquest.server.particle;

import com.mcquest.server.instance.Instance;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.EntityTracker;
import net.minestom.server.network.packet.server.play.ParticlePacket;
import net.minestom.server.particle.Particle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ParticleEmitter {
    private static final double RANGE = 50.0;
    private Instance instance;
    private Pos position;

    public ParticleEmitter(ParticleEffect effect, Particle particle, Instance instance, Pos position) {
        this.instance = instance;
        this.position = position;
    }

    public ParticleEmitter(ParticleEffect effect, Instance instance, Pos position) {
        this(effect, Particle.DUST_COLOR_TRANSITION, instance, position);
    }

    public Pos getPosition() {
        return position;
    }

    public void setPosition(Pos position) {
        this.position = position;
    }

    public Instance getInstance() {
        return instance;
    }

    public void setInstance(Instance instance) {
        this.instance = instance;
    }

    void tick() {
        Collection<Player> nearbyPlayers = new ArrayList<>();
        instance.getEntityTracker().nearbyEntities(position, RANGE, EntityTracker.Target.PLAYERS, nearbyPlayers::add);
        // TODO
        Collection<ParticlePacket> packets = Collections.emptyList();
        nearbyPlayers.forEach(player ->
                packets.forEach(packet -> player.sendPacket(packet))
        );
    }
}
