package com.mcquest.server.npc;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.Character;
import com.mcquest.server.character.*;
import com.mcquest.server.constants.Models;
import com.mcquest.server.physics.Collider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.instance.Instance;
import team.unnamed.hephaestus.minestom.ModelEntity;

public class Dwarf extends NonPlayerCharacter {
    private static final Component DISPLAY_NAME = Component.text("Dwarf", NamedTextColor.GREEN);

    private final Mmorpg mmorpg;
    private final Pos spawnPosition;
    private final Collider hitbox;
    private Entity entity;

    public Dwarf(Mmorpg mmorpg, Instance instance, Pos spawnPosition) {
        super(DISPLAY_NAME, 25, instance, spawnPosition);
        this.mmorpg = mmorpg;
        this.spawnPosition = spawnPosition;
        this.hitbox = new CharacterHitbox(this, instance, spawnPosition, 1, 2, 1);
    }

    @Override
    public void spawn() {
        super.spawn();
        entity = new Entity(this);
        CharacterEntityManager characterEntityManager = mmorpg.getCharacterEntityManager();
        characterEntityManager.bind(entity, this);
        entity.setInstance(getInstance(), getPosition()).join();
        mmorpg.getPhysicsManager().addCollider(hitbox);
    }

    @Override
    public void despawn() {
        super.despawn();
        CharacterEntityManager characterEntityManager = mmorpg.getCharacterEntityManager();
        characterEntityManager.unbind(entity);
        entity.remove();
        setPosition(spawnPosition);
        mmorpg.getPhysicsManager().removeCollider(hitbox);
    }

    @Override
    public void setPosition(Pos position) {
        super.setPosition(position);
        hitbox.setCenter(position.add(0.0, getHeight() / 2.0, 0.0));
    }

    @Override
    public void damage(DamageSource source, double amount) {
        super.damage(source, amount);
        if (!isAlive()) {
            if (source instanceof PlayerCharacter pc) {
                pc.grantExperiencePoints(50);
            }
        }
    }

    @Override
    public boolean isFriendly(Character character) {
        return false;
    }

    public static class Entity extends ModelEntity {
        private final Dwarf dwarf;

        public Entity(Dwarf dwarf) {
            super(Models.WOLF_SPIDER);
            this.dwarf = dwarf;
            setNoGravity(false);
            addAIGroup(new EntityAIGroupBuilder()
                    .addGoalSelector(new RandomStrollGoal(this, 20))
                    .build());
        }

        @Override
        public void tick(long time) {
            super.tick(time);
            dwarf.setPosition(getPosition());
        }
    }
}
