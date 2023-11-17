package net.mcquest.core.character;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.object.Object;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.core.stat.CharacterStats;
import net.mcquest.core.util.MathUtility;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.collision.BoundingBox;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * A Character represents an MMORPG character. Character is the superclass of
 * PlayerCharacter and NonPlayerCharacter.
 */
public abstract class Character extends Object implements DamageSource {
    public CharacterStats stats;
    private final CharacterHitbox hitbox;
    private final Nameplate nameplate;
    private String name;
    private int level;
    private double mass;
    private boolean invisible;
    private BossHealthBar bossHealthBar;

    Character(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner);
        nameplate = new Nameplate(this);
        hitbox = new CharacterHitbox(this, getInstance(), Pos.ZERO, Vec.ZERO);
        name = "";
        level = 1;
        stats = new CharacterStats();
        mass = 70;
        invisible = false;
    }

    @Override
    protected void spawn() {
        nameplate.spawn();
        hitbox.setExtents(hitboxExtents());
        getMmorpg().getPhysicsManager().addCollider(hitbox);
    }

    @Override
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
    public void setInstance(Instance instance, Pos position) {
        super.setInstance(instance, position);

        nameplate.updateInstance();
    }

    @Override
    public void setPosition(Pos position) {
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
        return stats.maxHealth;
    }

    /**
     * Sets the maximum health of this Character.
     */
    public void setMaxHealth(double maxHealth) {
        if (maxHealth <= 0.0) {
            throw new IllegalArgumentException();
        }

        stats.maxHealth = maxHealth;

        nameplate.updateHealthBarText();

        if (bossHealthBar != null) {
            bossHealthBar.updateHealth();
        }
    }

    /**
     * Returns the current health of this Character.
     */
    public final double getHealth() {
        return stats.health;
    }

    /**
     * Sets the current health of this Character.
     */
    public void setHealth(double health) {
        if (health < 0.0 || health > stats.maxHealth) {
            throw new IllegalArgumentException();
        }

        stats.health = health;

        nameplate.updateHealthBarText();

        if (bossHealthBar != null) {
            bossHealthBar.updateHealth();
        }
    }

    public final boolean isAlive() {
        return stats.health > 0.0;
    }

    public void damage(@NotNull DamageSource source, double amount) {
        if (amount < 0.0) {
            throw new IllegalArgumentException();
        }

        double newHealth = MathUtility.clamp(stats.health - amount, 0.0, stats.maxHealth);
        setHealth(newHealth);
    }

    public void heal(@NotNull DamageSource source, double amount) {
        if (amount < 0.0) {
            throw new IllegalArgumentException();
        }

        double newHealth = MathUtility.clamp(stats.health + amount, 0.0, stats.maxHealth);
        setHealth(newHealth);
    }

    public final double getMass() {
        return mass;
    }

    public final void setMass(double mass) {
        this.mass = mass;
    }

    /**
     * Movement speed in blocks per second.
     */
    public final double getMovementSpeed() {
        return getEntity().getAttributeValue(Attribute.MOVEMENT_SPEED) * 20.0;
    }

    public final void setMovementSpeed(double movementSpeed) {
        getEntity().getAttribute(Attribute.MOVEMENT_SPEED)
                .setBaseValue((float) (movementSpeed / 20.0));
    }

    public final boolean isInvisible() {
        return invisible;
    }

    public final void setInvisible(boolean invisible) {
        this.invisible = invisible;
        // TODO: this doesn't work for model entities.
        getEntity().setInvisible(invisible);
    }

    public Vec getVelocity() {
        return getEntity().getVelocity();
    }

    public void setVelocity(Vec velocity) {
        getEntity().setVelocity(velocity);
    }

    /**
     * @param impulse the impulse in kg m/s
     */
    public final void applyImpulse(Vec impulse) {
        getEntity().setVelocity(getVelocity().add(impulse.div(mass)));
    }

    public final BossHealthBar getBossHealthBar() {
        if (bossHealthBar == null) {
            bossHealthBar = new BossHealthBar(this);
        }

        return bossHealthBar;
    }

    public final void emitSound(Sound sound) {
        getInstance().playSound(sound, getPosition());
    }

    public final void speak(Collection<PlayerCharacter> pcs, Component message) {
        for (PlayerCharacter pc : pcs) {
            speak(pc, message);
        }
    }

    public abstract LivingEntity getEntity();

    public void speak(PlayerCharacter pc, Component message) {
        pc.sendMessage(formatMessage(pc, message));
    }

    private TextComponent formatMessage(PlayerCharacter pc, Component message) {
        return Component.empty()
                .append(Component.text("[", NamedTextColor.GRAY))
                .append(Component.text(name, getAttitude(pc).getColor()))
                .append(Component.text("] ", NamedTextColor.GRAY))
                .append(message);
    }

    public Attitude getAttitude(@NotNull Character other) {
        return Attitude.FRIENDLY;
    }

    public boolean isDamageable(@NotNull DamageSource source) {
        return false;
    }

    public final Pos getTargetBlockPosition(double maxDistance) {
        Point blockPosition = getEntity().getTargetBlockPosition((int) maxDistance);
        if (blockPosition == null) {
            return null;
        }

        Block block = getInstance().getBlock(blockPosition);
        if (!block.isSolid()) {
            return null;
        }

        return Pos.fromPoint(blockPosition).add(0.5, 1.0, 0.5);
    }

    public final Vec getLookDirection() {
        return getPosition().direction();
    }

    public final void setLookDirection(Vec lookDirection) {
        setPosition(getPosition().withDirection(lookDirection));
    }

    public final void lookAt(Pos position) {
        getEntity().lookAt(position);
    }

    public final void lookAt(Character character) {
        getEntity().lookAt(character.getEntity());
    }

    public final boolean hasLineOfSight(Character other, boolean exactView) {
        return getEntity().hasLineOfSight(other.getEntity(), exactView);
    }

    public final boolean isOnGround() {
        return getEntity().isOnGround();
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
        double ratio = stats.health / stats.maxHealth;
        int numRedBars = (int) Math.ceil(numBars * ratio);
        int numGrayBars = numBars - numRedBars;
        return Component.text("[", NamedTextColor.GRAY)
                .append(Component.text("|".repeat(numRedBars), NamedTextColor.RED))
                .append(Component.text("|".repeat(numGrayBars), NamedTextColor.GRAY))
                .append(Component.text("]", NamedTextColor.GRAY));
    }
}
