package com.mcquest.server.npc;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.CharacterEntityManager;
import com.mcquest.server.character.CharacterHitbox;
import com.mcquest.server.character.NonPlayerCharacter;
import com.mcquest.server.physics.PhysicsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityCreature;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.entity.metadata.other.SlimeMeta;
import net.minestom.server.instance.Instance;

public class GelatinousCube extends NonPlayerCharacter {
    private static final Component DISPLAY_NAME =
            Component.text("Gelatinous Cube", NamedTextColor.RED);

    private final Mmorpg mmorpg;
    private final Pos spawnPosition;
    private final CharacterHitbox hitbox;
    private Entity entity;

    public GelatinousCube(Mmorpg mmorpg, Instance instance, Pos spawnPosition) {
        super(DISPLAY_NAME, 15, instance, spawnPosition);
        this.mmorpg = mmorpg;
        this.spawnPosition = spawnPosition;
        hitbox = new CharacterHitbox(this, instance, spawnPosition, 5, 5, 5);
        entity = null;
        setHeight(3.0);
    }

    @Override
    protected void spawn() {
        super.spawn();
        entity = new Entity(this);
        CharacterEntityManager characterEntityManager = mmorpg.getCharacterEntityManager();
        characterEntityManager.bind(entity, this);
        entity.setInstance(getInstance(), getPosition());
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

    public static class Entity extends EntityCreature {
        private final GelatinousCube gelatinousCube;
        private long nextJumpTime;

        private Entity(GelatinousCube gelatinousCube) {
            super(EntityType.SLIME);
            this.gelatinousCube = gelatinousCube;
            ((SlimeMeta) getEntityMeta()).setSize(5);
            getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.1f);
            addAIGroup(new EntityAIGroupBuilder()
                    .addGoalSelector(new RandomStrollGoal(this, 10))
                    .build());
            nextJumpTime = System.currentTimeMillis();
        }

        @Override
        public void tick(long time) {
            super.tick(time);
            gelatinousCube.setPosition(getPosition());
            if (System.currentTimeMillis() >= nextJumpTime) {
                getNavigator().jump(4.5f);
                nextJumpTime += 1000l;
            }
        }
    }
}
