package com.mcquest.server.character;

import com.mcquest.server.instance.Instance;
import com.mcquest.server.util.MathUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.hologram.Hologram;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

/**
 * A Character represents an MMORPG character. Character is the superclass of
 * PlayerCharacter and NonPlayerCharacter.
 */
public class Character implements DamageSource {
    private Component displayName;
    private int level;
    private double health;
    private double maxHealth;
    private Instance instance;
    private Pos position;
    private double height;
    private Hologram nameplate;
    private Hologram healthBar;

    Character(@NotNull Component displayName, int level,
              @NotNull Instance instance, @NotNull Pos position) {
        if (level < 1) {
            throw new IllegalArgumentException();
        }
        this.displayName = displayName;
        this.level = level;
        this.maxHealth = 1.0;
        this.health = maxHealth;
        this.instance = instance;
        this.position = position;
        // Default is humanoid height.
        height = 2.0;
        nameplate = null;
        healthBar = null;
    }

    /**
     * Returns the name of this Character.
     */
    @Override
    public final @NotNull Component getDisplayName() {
        return displayName;
    }

    /**
     * Sets the name of this Character.
     */
    @MustBeInvokedByOverriders
    public void setDisplayName(@NotNull Component displayName) {
        this.displayName = displayName;
        updateNameplateText();
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
    @MustBeInvokedByOverriders
    public void setLevel(int level) {
        this.level = level;
    }

    public final Instance getInstance() {
        return instance;
    }

    @MustBeInvokedByOverriders
    public void setInstance(@NotNull Instance instance) {
        if (instance == this.instance) {
            return;
        }

        // Update nameplate and health bar instance.
        boolean nameplateAndHealthBarVisible = nameplateAndHealthBarVisible();
        if (nameplateAndHealthBarVisible) {
            hideNameplateAndHealthBar();
        }

        this.instance = instance;

        if (nameplateAndHealthBarVisible) {
            showNameplateAndHealthBar();
        }
    }

    public final Pos getPosition() {
        return position;
    }

    @MustBeInvokedByOverriders
    public void setPosition(@NotNull Pos position) {
        this.position = position;
        updateNameplateAndHealthBarPosition();
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
        updateHealthBarText();
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
        updateHealthBarText();
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

    @MustBeInvokedByOverriders
    public void setHeight(double height) {
        this.height = height;
        updateNameplateAndHealthBarPosition();
    }

    boolean nameplateAndHealthBarVisible() {
        return healthBar != null;
    }

    void showNameplateAndHealthBar() {
        nameplate = new Hologram(instance, nameplatePosition(), nameplateText());
        healthBar = new Hologram(instance, healthBarPosition(), healthBarText());
    }

    void hideNameplateAndHealthBar() {
        nameplate.remove();
        healthBar.remove();
        nameplate = null;
        healthBar = null;
    }

    public boolean isFriendly(@NotNull Character other) {
        return false;
    }

    private void updateNameplateAndHealthBarPosition() {
        if (nameplateAndHealthBarVisible()) {
            nameplate.setPosition(nameplatePosition());
            healthBar.setPosition(healthBarPosition());
        }
    }

    private Pos nameplatePosition() {
        return position.add(0.0, height + 0.25, 0.0);
    }

    private Pos healthBarPosition() {
        return position.add(0.0, height, 0.0);
    }

    private void updateNameplateText() {
        if (nameplateAndHealthBarVisible()) {
            nameplate.setText(nameplateText());
        }
    }

    private void updateHealthBarText() {
        if (nameplateAndHealthBarVisible()) {
            healthBar.setText(healthBarText());
        }
    }

    private Component nameplateText() {
        return Component.text("[", NamedTextColor.GRAY)
                .append(Component.text("Lv. " + level, NamedTextColor.GOLD))
                .append(Component.text("] ", NamedTextColor.GRAY))
                .append(this.displayName);
    }

    private Component healthBarText() {
        int numBars = 20;
        double ratio = health / maxHealth;
        int numRedBars = (int) Math.ceil(numBars * ratio);
        int numGrayBars = numBars - numRedBars;
        return Component.text("[", NamedTextColor.GRAY)
                .append(Component.text("|".repeat(numRedBars), NamedTextColor.RED))
                .append(Component.text("|".repeat(numGrayBars), NamedTextColor.GRAY))
                .append(Component.text("]", NamedTextColor.GRAY));
    }
}
