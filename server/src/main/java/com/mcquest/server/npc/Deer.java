package com.mcquest.server.npc;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.CharacterEntityManager;
import com.mcquest.core.character.CharacterHitbox;
import com.mcquest.core.character.DamageSource;
import com.mcquest.core.character.NonPlayerCharacter;
import com.mcquest.core.instance.Instance;
import com.mcquest.core.physics.Collider;
import com.mcquest.core.physics.PhysicsManager;
import com.mcquest.server.constants.Models;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.minestom.ModelEntity;

public class Deer extends NonPlayerCharacter {
    private static final Vec SIZE = new Vec(1, 1.25, 1);

    private final Mmorpg mmorpg;
    private final Pos spawnPosition;
    private final Collider hitbox;
    private Entity entity;

    public Deer(Mmorpg mmorpg, Instance instance, Pos spawnPosition) {
        super(instance, spawnPosition);
        this.mmorpg = mmorpg;
        this.spawnPosition = spawnPosition;
        this.hitbox = new CharacterHitbox(this, instance, hitboxPosition(), SIZE);
        setName("Deer");
        setHeight(SIZE.y());
        setMaxHealth(10);
        setHealth(getMaxHealth());
    }

    @Override
    protected void spawn() {
        super.spawn();
        entity = new Entity(this);
        CharacterEntityManager characterEntityManager = mmorpg.getCharacterEntityManager();
        characterEntityManager.bind(entity, this);
        entity.setInstance(getInstance(), getPosition());
        mmorpg.getPhysicsManager().addCollider(hitbox);
    }

    @Override
    protected void despawn() {
        super.despawn();

        CharacterEntityManager characterEntityManager = mmorpg.getCharacterEntityManager();
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();

        characterEntityManager.unbind(entity);
        entity.remove();

        setPosition(spawnPosition);

        physicsManager.removeCollider(hitbox);
    }

    @Override
    public boolean isDamageable(DamageSource source) {
        return true;
    }

    private void updatePosition(@NotNull Pos position) {
        super.setPosition(position);
        hitbox.setCenter(hitboxPosition());
    }

    private Pos hitboxPosition() {
        return getPosition().withY(y -> y + SIZE.y() / 2.0);
    }

    @Override
    protected void onDamage(DamageSource source, double amount) {
        Sound sound = Sound.sound(SoundEvent.ENTITY_DONKEY_HURT, Sound.Source.NEUTRAL, 1f, 1f);
        getInstance().playSound(sound, getPosition());
    }

    @Override
    protected void onDeath(DamageSource killer) {
        Sound sound = Sound.sound(SoundEvent.ENTITY_DONKEY_DEATH, Sound.Source.NEUTRAL, 1f, 1f);
        getInstance().playSound(sound, getPosition());

        ModelEntity deathModel = new ModelEntity(Models.DEER);
        deathModel.playAnimation("walk");
        deathModel.setInstance(getInstance(), getPosition());

        SchedulerManager scheduler = mmorpg.getSchedulerManager();

        scheduler.buildTask(deathModel::remove).delay(TaskSchedule.millis(2000)).schedule();
        scheduler.buildTask(this::respawn).delay(TaskSchedule.seconds(3)).schedule();
    }

    private void respawn() {
        Deer deer = new Deer(mmorpg, getInstance(), spawnPosition);
        mmorpg.getObjectManager().add(deer);
    }

    public static class Entity extends ModelEntity {
        private final Deer deer;

        private Entity(Deer deer) {
            super(Models.DEER);
            this.deer = deer;
            playAnimation("walk");
            setBoundingBox(SIZE.x(), SIZE.y(), SIZE.z());
            getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.05f);
            addAIGroup(new EntityAIGroupBuilder()
                    .addGoalSelector(new RandomStrollGoal(this, 10))
                    .build());
        }

        @Override
        public void update(long time) {
            super.update(time);
            if (deer.isSpawned()) {
                deer.updatePosition(getPosition());
            }
        }
    }
}
