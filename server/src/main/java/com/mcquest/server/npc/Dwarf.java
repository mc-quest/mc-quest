package com.mcquest.server.npc;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.*;
import com.mcquest.server.character.Character;
import com.mcquest.server.physics.Collider;
import com.mcquest.server.util.ResourceLoader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.instance.Instance;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.minestom.BoneEntity;
import team.unnamed.hephaestus.minestom.ModelEntity;
import team.unnamed.hephaestus.reader.ModelReader;
import team.unnamed.hephaestus.reader.blockbench.BBModelReader;

import java.io.IOException;

public class Dwarf extends NonPlayerCharacter {
    private static final Component DISPLAY_NAME = Component.text("Dwarf", NamedTextColor.GREEN);

    private static Model model;

    private final Mmorpg mmorpg;
    private final Pos spawnPosition;
    private final Collider hitbox;
    private Entity entity;

    public Dwarf(Mmorpg mmorpg, Instance instance, Pos spawnPosition) {
        super(DISPLAY_NAME, 25, instance, spawnPosition);
        this.mmorpg = mmorpg;
        this.spawnPosition = spawnPosition;
        this.hitbox = new CharacterHitbox(this, instance, spawnPosition, 1, 2, 1);
        if (model == null) {
            ModelReader reader = BBModelReader.blockbench();
            try {
                model = reader.read(ResourceLoader.getResourceAsStream("models/baby_wolf_spider.bbmodel"));
                mmorpg.getResourceManager().addModel(model);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
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
            super(EntityType.ARMOR_STAND, model);
            ArmorStandMeta meta = (ArmorStandMeta) getEntityMeta();
            meta.setSmall(true);
            meta.setMarker(false);
            this.dwarf = dwarf;
            // setNoGravity(false);
//            addAIGroup(new EntityAIGroupBuilder()
//                    .addGoalSelector(new RandomStrollGoal(this, 20))
//                    .build());
//            getNavigator().getPathingEntity().setAvian(true);
        }

        @Override
        public void tick(long time) {
            super.tick(time);
            this.tickAnimations();
            if (dwarf.getPosition().equals(position)) {
                setAnimation("idle");
            } else {
                setAnimation("walk");
            }
            dwarf.setPosition(getPosition());
        }

        private String animation = "idle";

        private void setAnimation(String animation) {
            if (this.animation.equals(animation)) {
                return;
            }
            this.animation = animation;
            // playAnimation(animation);
        }

        @Override
        public void remove() {
            super.remove();
            for (BoneEntity bone : bones()) {
                bone.remove();
            }
        }
    }
}
