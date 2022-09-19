package com.mcquest.server.character;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.EntityTracker;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NonPlayerCharacter extends Character {
    private boolean isSpawned;

    public NonPlayerCharacter(@NotNull Component displayName, int level,
                              @NotNull Instance instance, @NotNull Pos position) {
        super(displayName, level, instance, position);
        isSpawned = false;
    }

    public final boolean isSpawned() {
        return isSpawned;
    }

    @MustBeInvokedByOverriders
    protected void spawn() {
        isSpawned = true;
        showNameplateAndHealthBar();
    }

    @MustBeInvokedByOverriders
    protected void despawn() {
        isSpawned = false;
        hideNameplateAndHealthBar();
    }

    @MustBeInvokedByOverriders
    @Override
    public void damage(DamageSource source, double amount) {
        super.damage(source, amount);
        if (getHealth() == 0) {
            despawn();
        }
    }

    protected boolean shouldSpawn() {
        return playerNearby(50.0);
    }

    protected boolean shouldDespawn() {
        return !playerNearby(60.0);
    }

    private boolean playerNearby(double range) {
        Instance instance = getInstance();
        Pos position = getPosition();
        List<Player> nearbyPlayers = new ArrayList<>();
        instance.getEntityTracker().nearbyEntities(position, range,
                EntityTracker.Target.PLAYERS, nearbyPlayers::add);
        return !nearbyPlayers.isEmpty();
    }
}
