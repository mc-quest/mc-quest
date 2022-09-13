package com.mcquest.server.npc;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.*;
import com.mcquest.server.character.Character;
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
    private static final Component DISPLAY_NAME = Component.text("Zombie",
            NamedTextColor.GREEN);

    private final Mmorpg mmorpg;
    private final PlayerCharacter summoner;
    private Entity entity;

    public NecromancerZombie(Mmorpg mmorpg, PlayerCharacter summoner,
                             Instance instance, Pos spawnPosition) {
        super(DISPLAY_NAME, 1, instance, spawnPosition);
        this.mmorpg = mmorpg;
        this.summoner = summoner;
    }

    @Override
    protected void spawn() {
        super.spawn();
        entity = new Entity(this);
        CharacterEntityManager characterEntityManager = mmorpg.getCharacterEntityManager();
        characterEntityManager.bind(entity, this);
        entity.setInstance(getInstance(), getPosition()).join();
    }

    @Override
    protected void despawn() {
        super.despawn();
        CharacterEntityManager characterEntityManager = mmorpg.getCharacterEntityManager();
        characterEntityManager.unbind(entity);
        entity.remove();
    }

    @Override
    public void damage(DamageSource source, double amount) {
        super.damage(source, amount);
        entity.damage(DamageType.VOID, 0f);
    }

    @Override
    public boolean isFriendly(Character other) {
        if (other instanceof PlayerCharacter) {
            return true;
        }
        return other.isFriendly(summoner);
    }

    public static class Entity extends EntityCreature {
        private final NecromancerZombie zombie;

        public Entity(NecromancerZombie zombie) {
            super(EntityType.ZOMBIE);
            this.zombie = zombie;
            addAIGroup(new EntityAIGroupBuilder()
                    .addTargetSelector(new ClosestEntityTarget(this, 20, this::shouldTarget))
                    .addGoalSelector(new MeleeAttackGoal(this, 1.2,
                            Duration.ofSeconds(1)))
                    .build());
        }

        private boolean shouldTarget(net.minestom.server.entity.Entity entity) {
            CharacterEntityManager characterEntityManager = zombie.mmorpg.getCharacterEntityManager();
            Character character = characterEntityManager.getCharacter(entity);
            return character != null && !zombie.isFriendly(character);
        }

        @Override
        public void tick(long time) {
            super.tick(time);
            zombie.setPosition(getPosition());
        }
    }
}
