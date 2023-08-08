package com.mcquest.core.character;

import com.mcquest.core.instance.Instance;
import com.mcquest.core.object.Object;
import com.mcquest.core.util.MathUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

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
    private Nameplate nameplate;
    private BossHealthBar bossHealthBar;

    Character(@NotNull Instance instance, @NotNull Pos position) {
        super(instance, position);
        name = "";
        level = 1;
        maxHealth = 1.0;
        health = maxHealth;
        // Default is humanoid height.
        height = 2.0;
    }

    @Override
    @MustBeInvokedByOverriders
    protected void spawn() {
        super.spawn();

        nameplate = new Nameplate(this);
        nameplate.spawn();
    }

    @Override
    @MustBeInvokedByOverriders
    protected void despawn() {
        super.despawn();

        nameplate.despawn();
        nameplate = null;
    }

    public final String getName() {
        return name;
    }

    public final void setName(@NotNull String name) {
        this.name = name;

        if (isSpawned()) {
            nameplate.updateNameText();
        }

        if (bossHealthBar != null) {
            bossHealthBar.updateText();
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

        if (bossHealthBar != null) {
            bossHealthBar.updateText();
        }
    }

    @Override
    @MustBeInvokedByOverriders
    public void setInstance(@NotNull Instance instance, Pos position) {
        super.setInstance(instance, position);

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

        if (bossHealthBar != null) {
            bossHealthBar.updateHealth();
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

        if (bossHealthBar != null) {
            bossHealthBar.updateHealth();
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

    public BossHealthBar getBossHealthBar() {
        if (bossHealthBar == null) {
            bossHealthBar = new BossHealthBar(this);
        }

        return bossHealthBar;
    }

    public final void speak(Collection<PlayerCharacter> pcs, Component message) {
        for (PlayerCharacter pc : pcs) {
            speak(pc, message);
        }
    }

    @MustBeInvokedByOverriders
    public void speak(PlayerCharacter pc, Component message) {
        pc.sendMessage(formatMessage(pc, message));
    }

    private TextComponent formatMessage(PlayerCharacter pc, Component message) {
        return Component.empty()
                .append(Component.text("[", NamedTextColor.GRAY))
                .append(Component.text(name, getAttitude(pc).getColor()))
                .append(Component.text("]: ", NamedTextColor.GRAY))
                .append(message);
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

    TextComponent nameText(Attitude attitude) {
        return Component.text("[", NamedTextColor.GRAY)
                .append(Component.text("Lv. " + level, NamedTextColor.GOLD))
                .append(Component.text("] ", NamedTextColor.GRAY))
                .append(Component.text(name, attitude.getColor()));
    }

    TextComponent healthBarText() {
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
