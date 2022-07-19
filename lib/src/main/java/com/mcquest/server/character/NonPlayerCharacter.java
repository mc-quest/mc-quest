package com.mcquest.server.character;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public class NonPlayerCharacter extends Character {
    private boolean isSpawned;

    public NonPlayerCharacter(Component displayName, int level,
                              Instance instance, Pos position) {
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
        return PlayerCharacter.isNearby(getInstance(), getPosition(), 50.0);
    }

    protected boolean shouldDespawn() {
        return !PlayerCharacter.isNearby(getInstance(), getPosition(), 60.0);
    }
}
