package com.mcquest.core.character;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.instance.Instance;
import com.mcquest.core.object.Object;
import com.mcquest.core.object.ObjectSpawner;
import com.mcquest.core.util.MathUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * A Character represents an MMORPG character. Character is the superclass of
 * PlayerCharacter and NonPlayerCharacter.
 */
public abstract class Character extends Object implements DamageSource {
    private final CharacterHitbox hitbox;
    private final Nameplate nameplate;
    private String name;
    private int level;
    private double maxHealth;
    private double health;
    private double mass;
    private BossHealthBar bossHealthBar;

    Character(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner);
        nameplate = new Nameplate(this);
        hitbox = new CharacterHitbox(this, getInstance(), Pos.ZERO, Vec.ZERO);
        name = "";
        level = 1;
        maxHealth = 1.0;
        mass = 70;
    }

    @Override
    @MustBeInvokedByOverriders
    protected void spawn() {
        nameplate.spawn();
        hitbox.setExtents(hitboxExtents());
        getMmorpg().getPhysicsManager().addCollider(hitbox);
    }

    @Override
    @MustBeInvokedByOverriders
    protected void despawn() {
        nameplate.despawn();

        hitbox.remove();

        if (bossHealthBar != null) {
            bossHealthBar.remove();
        }
    }

    public final String getName() {
        return name;
    }

    public final void setName(@NotNull String name) {
        this.name = name;

        nameplate.updateNameText();

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

        nameplate.updateNameText();

        if (bossHealthBar != null) {
            bossHealthBar.updateText();
        }
    }

    @Override
    @MustBeInvokedByOverriders
    public void setInstance(@NotNull Instance instance, Pos position) {
        super.setInstance(instance, position);

        nameplate.updateInstance();
    }

    @Override
    public void setPosition(@NotNull Pos position) {
        // TODO: fix PlayerCharacter implementation
        updatePosition(position);

        getEntity().teleport(position);
    }

    void updatePosition(Pos position) {
        super.setPosition(position);

        hitbox.setCenter(hitboxCenter());

        nameplate.updatePosition();
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

        nameplate.updateHealthBarText();

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

        nameplate.updateHealthBarText();

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

    public final double getMass() {
        return mass;
    }

    public final void setMass(double mass) {
        this.mass = mass;
    }

    /**
     * @param impulse the impulse in kg m/s
     */
    public void applyImpulse(Vec impulse) {
        getEntity().setVelocity(impulse.div(mass));
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

    public abstract Entity getEntity();

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

    final CharacterHitbox getHitbox() {
        return hitbox;
    }

    private Pos hitboxCenter() {
        return getPosition().withY(y -> y + hitboxExtents().y() / 2.0);
    }

    private Vec hitboxExtents() {
        BoundingBox boundingBox = getEntity().getBoundingBox();
        return new Vec(boundingBox.maxX() - boundingBox.minX(),
                boundingBox.maxY() - boundingBox.minY(),
                boundingBox.maxZ() - boundingBox.minZ());
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
