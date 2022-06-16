package com.mcquest.server.main.npc;

import com.mcquest.server.api.character.DamageSource;
import com.mcquest.server.api.character.NonPlayerCharacter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.goal.MeleeAttackGoal;
import net.minestom.server.entity.ai.target.ClosestEntityTarget;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.instance.Instance;

import java.time.Duration;

public class NecromancerZombie extends NonPlayerCharacter {
    private static final Component DISPLAY_NAME = Component.text("Zombie", NamedTextColor.GREEN);

    private final Pos spawnPosition;
    private Entity entity;

    public NecromancerZombie(Instance instance, Pos spawnPosition) {
        super(DISPLAY_NAME, 1, instance, spawnPosition);
        this.spawnPosition = spawnPosition;
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
        private final NecromancerZombie zombie;

        public Entity(NecromancerZombie zombie) {
            super(EntityType.ZOMBIE);
            this.zombie = zombie;
            addAIGroup(new EntityAIGroupBuilder()
                    .addTargetSelector(new ClosestEntityTarget(this, 20, GelatinousCube.Entity.class))
                    .addGoalSelector(new MeleeAttackGoal(this, 1.2, Duration.ofSeconds(1)))
                    .build());
        }

        @Override
        public void tick(long time) {
            super.tick(time);
            zombie.setPosition(getPosition());
        }
    }
}
