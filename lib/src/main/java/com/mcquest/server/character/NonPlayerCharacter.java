package com.mcquest.server.character;

import com.mcquest.server.instance.Instance;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.EntityTracker;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class NonPlayerCharacter extends Character {
    private boolean isSpawned;

    public NonPlayerCharacter(@NotNull Component displayName, int level,
                              @NotNull Instance instance, @NotNull Pos position) {
        super(displayName, level, instance, position);
        isSpawned = false;
    }

    @MustBeInvokedByOverriders
    @Override
    public void damage(DamageSource source, double amount) {
        super.damage(source, amount);
        if (getHealth() == 0) {
            despawn();
        }
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
