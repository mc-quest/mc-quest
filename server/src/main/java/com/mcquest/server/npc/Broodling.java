package com.mcquest.server.npc;

import com.mcquest.server.character.CharacterEntityManager;
import com.mcquest.server.character.CharacterHitbox;
import com.mcquest.server.character.NonPlayerCharacter;
import com.mcquest.server.character.PlayerCharacter;
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
import net.minestom.server.event.entity.EntityAttackEvent;
import net.minestom.server.instance.Instance;

import java.time.Duration;

public class Broodling extends NonPlayerCharacter {
    private static final Component DISPLAY_NAME =
            Component.text("Broodling", NamedTextColor.RED);
    private final Pos spawnPosition;
    private final CharacterHitbox hitbox;
    private Entity entity;

    public Broodling(Instance instance, Pos spawnPosition) {
        super(DISPLAY_NAME, 5, instance, spawnPosition);
        setHeight(1.0);
        hitbox = new CharacterHitbox(this, instance, spawnPosition, 1.5, getHeight(), 1.5);
        this.spawnPosition = spawnPosition;
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
        entity.setInstance(getInstance(), spawnPosition);
        CharacterEntityManager.register(entity, this);
        // hitbox.setEnabled(true);
    }

    @Override
    protected void despawn() {
        super.despawn();
        // hitbox.setEnabled(false);
        CharacterEntityManager.unregister(entity);
        entity.remove();
        setPosition(spawnPosition);
    }

    public static class Entity extends EntityCreature {
        private final Broodling broodling;

        private Entity(Broodling broodling) {
            super(EntityType.SPIDER);
            this.broodling = broodling;

            addAIGroup(new EntityAIGroupBuilder()
                    .addTargetSelector(new ClosestEntityTarget(this, 10,
                            entity -> entity instanceof Player))
                    .addGoalSelector(new MeleeAttackGoal(this, 1, Duration.ofSeconds(1)))
                    .addGoalSelector(new RandomStrollGoal(this, 5))
                    .build());

            eventNode().addListener(EntityAttackEvent.class, event -> {
                if (event.getTarget() instanceof Player player) {
                    PlayerCharacter pc = PlayerCharacter.forPlayer(player);
                    pc.damage(broodling, 0.1);
                }
            });
        }

        @Override
        public void tick(long time) {
            super.tick(time);
            broodling.setPosition(getPosition());
        }

        public Broodling getBroodling() {
            return broodling;
        }
    }
}
