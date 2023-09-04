package com.mcquest.core.character;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.ai.BehaviorNode;
import com.mcquest.core.ai.Navigator;
import com.mcquest.core.object.ObjectSpawner;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.minestom.ModelEntity;

import java.time.Duration;

public class NonPlayerCharacter extends Character {
    private final EntityCreature entity;
    private final Navigator navigator;
    private BehaviorNode brain;
    private Duration respawnDuration;
    private Task endDamageTint;

    public NonPlayerCharacter(Mmorpg mmorpg, ObjectSpawner spawner, CharacterModel model) {
        super(mmorpg, spawner);
        entity = model.createEntity(this);
        navigator = new Navigator(this);
    }

    public final void setLevel(int level) {
        super.setLevel(level);
    }

    @Override
    public EntityCreature getEntity() {
        return entity;
    }

    public Navigator getNavigator() {
        return navigator;
    }

    public final void setBrain(BehaviorNode brain) {
        this.brain = brain;
    }

    public Duration getRemovalDelay() {
        return Duration.ofMillis(entity.getRemovalAnimationDelay());
    }

    public final void setRemovalDelay(Duration removalDelay) {
        entity.setRemovalAnimationDelay((int) removalDelay.toMillis());
    }

    public final void setRespawnDuration(Duration respawnDuration) {
        this.respawnDuration = respawnDuration;
    }

    @Override
    protected final void spawn() {
        setHealth(getMaxHealth());
        super.spawn();
        entity.setInstance(getInstance(), getPosition());
    }

    @Override
    protected final void despawn() {
        super.despawn();
        entity.remove();
    }

    void tick(long time) {
        if (brain != null) {
            brain.tick(time);
        }

        updatePosition(entity.getPosition());
    }

    @Override
    public final void damage(@NotNull DamageSource source, double amount) {
        super.damage(source, amount);

        if (getHealth() == 0.0) {
            die(source);
        } else {
            takeDamage(source);
        }
    }

    @Override
    public final void heal(@NotNull DamageSource source, double amount) {
        super.heal(source, amount);

        onHeal(source);
    }

    public void lookAt(Pos position) {
        entity.lookAt(position);
    }

    public void lookAt(Character character) {
        entity.lookAt(character.getEntity());
    }

    public final void swingMainHand() {
        entity.swingMainHand();
    }

    public final void swingOffHand() {
        entity.swingOffHand();
    }

    public final void playAnimation(@Nullable String animation) {
        if (!(entity instanceof ModelEntity modelEntity)) {
            throw new IllegalStateException();
        }

        if (animation == null) {
            modelEntity.animationController().clearQueue();
            return;
        }

        if (!modelEntity.model().animations().containsKey(animation)) {
            throw new IllegalArgumentException();
        }

        modelEntity.playAnimation(animation);
    }

    /**
     * Convenience method invoked when this NonPlayerCharacter takes damage but
     * does not die as a result.
     */
    protected void onDamage(DamageSource source) {
    }

    /**
     * Convenience method invoked when this NonPlayerCharacter is healed.
     */
    protected void onHeal(DamageSource source) {
    }

    /**
     * Convenience method invoked when this NonPlayerCharacter takes damage and
     * dies as a result.
     */
    protected void onDeath(DamageSource killer) {
    }

    /**
     * Convenience method invoked when this NonPlayerCharacter is interacted
     * with (right-clicked).
     */
    protected void onInteract(PlayerCharacter pc) {
    }

    /**
     * Convenience method invoked when this NonPlayerCharacter speaks.
     */
    protected void onSpeak(PlayerCharacter pc) {
    }

    private void takeDamage(DamageSource source) {
        damageEffect();
        onDamage(source);
    }

    private void die(DamageSource killer) {
        deathEffect();
        entity.kill();
        onDeath(killer);
        SchedulerManager scheduler = getMmorpg().getSchedulerManager();
        getSpawner().remove();
        getHitbox().remove();
        scheduler.buildTask(this::remove).delay(getRemovalDelay()).schedule();
        if (respawnDuration != null) {
            scheduler.buildTask(this::respawn)
                    .delay(respawnDuration.plus(getRemovalDelay()))
                    .schedule();
        }
    }

    private void damageEffect() {
        if (entity instanceof ModelEntity modelEntity) {
            modelDamageTint(modelEntity);
        } else {
            entity.damage(DamageType.VOID, 0.0f);
        }
    }

    private void deathEffect() {
        if (entity instanceof ModelEntity modelEntity) {
            modelDamageTint(modelEntity);
        } else {
            entity.kill();
        }
    }

    private void modelDamageTint(ModelEntity modelEntity) {
        modelEntity.colorize(0xffaaaa);
        if (endDamageTint != null) {
            endDamageTint.cancel();
        }
        endDamageTint = getMmorpg().getSchedulerManager()
                .buildTask(modelEntity::colorizeDefault)
                .delay(Duration.ofMillis(200))
                .schedule();
    }

    private void respawn() {
        getMmorpg().getObjectManager().add(getSpawner());
    }
}
