package com.mcquest.server.npc;

import com.mcquest.server.character.NonPlayerCharacter;
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

    private final Pos spawnPosition;
    private final CharacterCollider hitbox;
    private Entity entity;

    public GelatinousCube(Instance instance, Pos spawnPosition) {
        super(DISPLAY_NAME, 15, instance, spawnPosition);
        this.spawnPosition = spawnPosition;
        hitbox = new CharacterCollider(this, instance, spawnPosition, 5, 5, 5);
        entity = null;
        setHeight(3.0);
    }

    @Override
    protected void spawn() {
        super.spawn();
        hitbox.setEnabled(true);
        entity = new Entity(this);
        entity.setInstance(getInstance(), getPosition());
    }

    @Override
    protected void despawn() {
        super.despawn();
        hitbox.setEnabled(false);
        entity.remove();
        setPosition(spawnPosition);
    }

    @Override
    public boolean shouldSpawn() {
        return true;
    }

    @Override
    public boolean shouldDespawn() {
        return false;
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
