package com.mcquest.server.character;

import com.mcquest.server.instance.Instance;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

public class NonPlayerCharacter extends Character {
    private boolean isSpawned;
    NonPlayerCharacterSpawner spawner;

    public NonPlayerCharacter(@NotNull Component displayName, int level,
                              @NotNull Instance instance, @NotNull Pos position) {
        super(displayName, level, instance, position);
        isSpawned = false;
        spawner = null;
    }

    @MustBeInvokedByOverriders
    @Override
    public void setInstance(Instance instance) {
        if (spawner != null) {
            spawner.updateCell(this, instance, getPosition());
        }
        super.setInstance(instance);
    }

    @MustBeInvokedByOverriders
    @Override
    public void setPosition(Pos position) {
        if (spawner != null) {
            spawner.updateCell(this, getInstance(), position);
        }
        super.setPosition(position);
    }

    @MustBeInvokedByOverriders
    @Override
    public void damage(@NotNull DamageSource source, double amount) {
        double prevHealth = getHealth();
        super.damage(source, amount);
        double newHealth = getHealth();
        if (prevHealth != newHealth && newHealth == 0.0) {
            if (isSpawned) {
                despawn();
                spawner.handleDeath(this);
            }
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
}
