package com.mcquest.core.character;

import com.mcquest.core.instance.Instance;
import com.mcquest.core.object.Object;
import com.mcquest.core.util.MathUtility;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

/**
 * A Character represents an MMORPG character. Character is the superclass of
 * PlayerCharacter and NonPlayerCharacter.
 */
public class Character extends Object implements DamageSource {
    private String name;
    private int level;
    private double maxHealth;
    private double health;
    private double height;
    private final Nameplate nameplate;

    Character(@NotNull Instance instance, @NotNull Pos position) {
        super(instance, position);
        name = "";
        level = 1;
        maxHealth = 1.0;
        health = maxHealth;
        // Default is humanoid height.
        height = 2.0;
        nameplate = new Nameplate(this);
    }

    @Override
    @MustBeInvokedByOverriders
    protected void spawn() {
        super.spawn();
        nameplate.spawn();
    }

    @Override
    @MustBeInvokedByOverriders
    protected void despawn() {
        super.despawn();
        nameplate.despawn();
    }

    public final String getName() {
        return name;
    }

    public final void setName(@NotNull String name) {
        this.name = name;

        if (isSpawned()) {
            nameplate.updateNameText();
        }
    }

    /**
     * Returns the level of this Character.
     */
    public final int getLevel() {
        return level;
    }

    /**
     * Sets the level of this Character.
     */
    void setLevel(int level) {
        this.level = level;

        if (isSpawned()) {
            nameplate.updateNameText();
        }
    }

    @Override
    @MustBeInvokedByOverriders
    public void setInstance(@NotNull Instance instance) {
        super.setInstance(instance);

        if (isSpawned()) {
            nameplate.updateInstance();
        }
    }

    @Override
    @MustBeInvokedByOverriders
    public void setPosition(@NotNull Pos position) {
        super.setPosition(position);

        if (isSpawned()) {
            nameplate.updatePosition();
        }
    }

    /**
     * Returns the maximum health of this Character.
     */
    public final double getMaxHealth() {
        return maxHealth;
    }

    /**
     * Sets the maximum health of this Character.
     */
    @MustBeInvokedByOverriders
    public void setMaxHealth(double maxHealth) {
        if (maxHealth <= 0.0) {
            throw new IllegalArgumentException();
        }

        this.maxHealth = maxHealth;

        if (isSpawned()) {
            nameplate.updateHealthBarText();
        }
    }

    /**
     * Returns the current health of this Character.
     */
    public final double getHealth() {
        return health;
    }

    /**
     * Sets the current health of this Character.
     */
    @MustBeInvokedByOverriders
    public void setHealth(double health) {
        if (health < 0.0 || health > maxHealth) {
            throw new IllegalArgumentException();
        }

        this.health = health;

        if (isSpawned()) {
            nameplate.updateHealthBarText();
        }
    }

    public final boolean isAlive() {
        return health > 0.0;
    }

    @MustBeInvokedByOverriders
    public void damage(@NotNull DamageSource source, double amount) {
        if (amount < 0.0) {
            throw new IllegalArgumentException();
        }

        double newHealth = MathUtility.clamp(health - amount, 0.0, maxHealth);
        setHealth(newHealth);
    }

    @MustBeInvokedByOverriders
    public void heal(@NotNull DamageSource source, double amount) {
        if (amount < 0.0) {
            throw new IllegalArgumentException();
        }

        double newHealth = MathUtility.clamp(health + amount, 0.0, maxHealth);
        setHealth(newHealth);
    }

    public final double getHeight() {
        return height;
    }

    public final void setHeight(double height) {
        this.height = height;

        if (isSpawned()) {
            nameplate.updatePosition();
        }
    }

    public Attitude getAttitude(@NotNull Character other) {
        return Attitude.FRIENDLY;
    }

    public boolean isDamageable(@NotNull DamageSource source) {
        return false;
    }

    final Nameplate getNameplate() {
        return nameplate;
    }
}
