package com.mcquest.server.npc;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.*;
import com.mcquest.server.character.Character;
import com.mcquest.server.physics.PhysicsManager;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.entity.metadata.animal.RabbitMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.SchedulerManager;

import java.time.Duration;

public class Rabbit extends NonPlayerCharacter {
    private static final Component DISPLAY_NAME =
            Component.text("Rabbit", NamedTextColor.GREEN);
    private static final Sound HURT_SOUND =
            Sound.sound(SoundEvent.ENTITY_RABBIT_HURT, Sound.Source.NEUTRAL, 1f, 1f);
    private static final Sound DEATH_SOUND =
            Sound.sound(SoundEvent.ENTITY_RABBIT_DEATH, Sound.Source.NEUTRAL, 1f, 1f);

    private final Mmorpg mmorpg;
    private final Pos spawnPosition;
    private final RabbitMeta.Type type;
    private final CharacterHitbox hitbox;
    private Entity entity;

    public Rabbit(Mmorpg mmorpg, Instance instance, Pos spawnPosition, RabbitMeta.Type type) {
        super(DISPLAY_NAME, 1, instance, spawnPosition);
        this.mmorpg = mmorpg;
        this.spawnPosition = spawnPosition;
        this.type = type;
        hitbox = new CharacterHitbox(this, instance, spawnPosition, 0.5, 0.5, 0.5);
        setHeight(0.5);
    }

    @Override
    public void setPosition(Pos position) {
        super.setPosition(position);
        hitbox.setCenter(position.add(0.0, getHeight() / 2.0, 0.0));
    }

    @Override
    protected void spawn() {
        super.spawn();
        entity = new Entity(this);
        CharacterEntityManager characterEntityManager = mmorpg.getCharacterEntityManager();
        characterEntityManager.bind(entity, this);
        entity.setInstance(getInstance(), getPosition()).join();
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();
        physicsManager.addCollider(hitbox);
    }

    @Override
    protected void despawn() {
        super.despawn();
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();
        physicsManager.removeCollider(hitbox);
        CharacterEntityManager characterEntityManager = mmorpg.getCharacterEntityManager();
        characterEntityManager.unbind(entity);
        entity.remove();
        setPosition(spawnPosition);
    }

    @Override
    public void damage(DamageSource source, double amount) {
        Pos position = getPosition();
        super.damage(source, amount);
        entity.damage(DamageType.VOID, 0f);
        if (getHealth() == 0) {
            die(position);
        } else {
            getInstance().playSound(HURT_SOUND, position.x(), position.y(), position.z());
        }
    }

    private void die(Pos position) {
        SchedulerManager schedulerManager = MinecraftServer.getSchedulerManager();
        getInstance().playSound(DEATH_SOUND, position.x(), position.y(), position.z());
        schedulerManager.buildTask(() -> {
            respawn();
        }).delay(Duration.ofSeconds(30)).schedule();
    }

    private void respawn() {
        setHealth(getMaxHealth());
    }

    @Override
    public boolean isFriendly(Character other) {
        return true;
    }

    public static class Entity extends EntityCreature {
        private final Rabbit rabbit;

        private Entity(Rabbit rabbit) {
            super(EntityType.RABBIT);
            ((RabbitMeta) getEntityMeta()).setType(rabbit.type);
            this.rabbit = rabbit;
            addAIGroup(new EntityAIGroupBuilder()
                    .addGoalSelector(new RandomStrollGoal(this, 5))
                    .build());
        }

        @Override
        public void tick(long time) {
            super.tick(time);
            rabbit.setPosition(getPosition());
        }

        public Rabbit getRabbit() {
            return rabbit;
        }
    }
}
