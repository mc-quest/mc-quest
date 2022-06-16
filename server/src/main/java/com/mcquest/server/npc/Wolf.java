package com.mcquest.server.npc;

import com.mcquest.server.character.DamageSource;
import com.mcquest.server.character.NonPlayerCharacter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.goal.MeleeAttackGoal;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.instance.Instance;

import java.time.Duration;

public class Wolf extends NonPlayerCharacter {
    private static final Component DISPLAY_NAME =
            Component.text("Wolf", NamedTextColor.RED);

    private final Pos spawnPosition;
    private Entity entity;

    public Wolf(Instance instance, Pos spawnPosition) {
        super(DISPLAY_NAME, 7, instance, spawnPosition);
        this.spawnPosition = spawnPosition;
        setHeight(1.0);
    }

    @Override
    protected void spawn() {
        super.spawn();
        entity = new Entity(this);
        entity.setInstance(getInstance(), getPosition());
    }

    public void doSpawn() {
        spawn();
    }

    @Override
    protected void despawn() {
        super.despawn();
        entity.remove();
        setPosition(spawnPosition);
    }

    @Override
    public void damage(DamageSource source, double amount) {
        super.damage(source, amount);
        entity.damage(DamageType.VOID, 0f);
    }

    @Override
    protected boolean shouldSpawn() {
        return true;
    }

    @Override
    protected boolean shouldDespawn() {
        return false;
    }

    public static class Entity extends EntityCreature {
        private final Wolf wolf;

        private Entity(Wolf wolf) {
            super(EntityType.WOLF);
            this.wolf = wolf;
            addAIGroup(new EntityAIGroupBuilder()
                    .addTargetSelector(new ClosestEntityTarget(this, 10, Rabbit.Entity.class))
                    .addGoalSelector(new MeleeAttackGoal(this, 1.2, Duration.ofSeconds(1)))
                    .addGoalSelector(new RandomStrollGoal(this, 5))
                    .build());
            eventNode().addListener(EntityAttackEvent.class, event -> {
                if (event.getTarget() instanceof Rabbit.Entity rabbitEntity) {
                    Rabbit rabbit = rabbitEntity.getRabbit();
                    rabbit.damage(wolf, 0.1);
                }
            });
        }

        @Override
        public void tick(long time) {
            super.tick(time);
            wolf.setPosition(getPosition());
        }
    }
}
