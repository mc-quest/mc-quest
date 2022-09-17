package com.mcquest.server.npc;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.Character;
import com.mcquest.server.character.CharacterEntityManager;
import com.mcquest.server.character.CharacterHitbox;
import com.mcquest.server.character.DamageSource;
import com.mcquest.server.character.NonPlayerCharacter;
import com.mcquest.server.physics.PhysicsManager;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.goal.MeleeAttackGoal;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.SchedulerManager;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class Wolf extends NonPlayerCharacter {
    private static final Component DISPLAY_NAME =
            Component.text("Wolf", NamedTextColor.RED);
    private static final Sound ATTACK_SOUND =
            Sound.sound(SoundEvent.ENTITY_WOLF_GROWL, Sound.Source.HOSTILE, 1f, 0.9f);
    private static final Sound HURT_SOUND =
            Sound.sound(SoundEvent.ENTITY_WOLF_HURT, Sound.Source.HOSTILE, 1f, 0.9f);
    private static final Sound DEATH_SOUND =
            Sound.sound(SoundEvent.ENTITY_WOLF_DEATH, Sound.Source.HOSTILE, 1f, 0.9f);

    private final Mmorpg mmorpg;
    private final Pos spawnPosition;
    private final CharacterHitbox hitbox;
    private Entity entity;

    public Wolf(Mmorpg mmorpg, Instance instance, Pos spawnPosition) {
        super(DISPLAY_NAME, 7, instance, spawnPosition);
        this.mmorpg = mmorpg;
        this.spawnPosition = spawnPosition;
        hitbox = new CharacterHitbox(this, instance, spawnPosition, 0.75, 0.75, 0.75);
        setHeight(0.75);
    }

    @Override
    public void setPosition(@NotNull Pos position) {
        super.setPosition(position);
        hitbox.setCenter(position.add(0.0, getHeight() / 2.0, 0.0));
    }

    @Override
    protected void spawn() {
        super.spawn();
        entity = new Entity(this);
        CharacterEntityManager characterEntityManager = mmorpg.getCharacterEntityManager();
        characterEntityManager.bind(entity, this);
        entity.setInstance(getInstance(), spawnPosition).join();
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();
        physicsManager.addCollider(hitbox);
    }

    @Override
    protected void despawn() {
        super.despawn();
        CharacterEntityManager characterEntityManager = mmorpg.getCharacterEntityManager();
        characterEntityManager.unbind(entity);
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();
        physicsManager.removeCollider(hitbox);
        if (isAlive()) {
            entity.remove();
        } else {
            entity.kill();
            Instance instance = getInstance();
            Pos position = getPosition();
            SchedulerManager schedulerManager = mmorpg.getSchedulerManager();
            schedulerManager.buildTask(() -> setHealth(getMaxHealth()))
                    .delay(Duration.ofSeconds(5)).schedule();
            instance.playSound(DEATH_SOUND, position);
        }
        entity = null;
        setPosition(spawnPosition);
    }

    @Override
    public void damage(DamageSource source, double amount) {
        entity.damage(DamageType.VOID, 0f);
        super.damage(source, amount);
        Instance instance = getInstance();
        if (isAlive()) {
            instance.playSound(HURT_SOUND);
        }
    }

    public static class Entity extends EntityCreature {
        private final Wolf wolf;

        private Entity(Wolf wolf) {
            super(EntityType.WOLF);
            this.wolf = wolf;
            addAIGroup(new EntityAIGroupBuilder()
                    .addTargetSelector(new ClosestEntityTarget(this, 5,
                            entity -> entity instanceof Rabbit.Entity || entity instanceof Player))
                    .addGoalSelector(new MeleeAttackGoal(this, 0.8, Duration.ofSeconds(2)))
                    .addGoalSelector(new RandomStrollGoal(this, 5))
                    .build());
            eventNode().addListener(EntityAttackEvent.class, event -> {
                getInstance().playSound(ATTACK_SOUND, position);
                CharacterEntityManager characterEntityManager = wolf.mmorpg.getCharacterEntityManager();
                Character target = characterEntityManager.getCharacter(event.getTarget());
                target.damage(wolf, 0.1);
            });
        }

        @Override
        public void tick(long time) {
            super.tick(time);
            wolf.setPosition(getPosition());
        }
    }
}
