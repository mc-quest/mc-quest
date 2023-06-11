package com.mcquest.server.npc;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.Character;
import com.mcquest.server.character.*;
import com.mcquest.server.constants.Models;
import com.mcquest.server.constants.Music;
import com.mcquest.server.instance.Instance;
import com.mcquest.server.audio.PlayerCharacterMusicPlayer;
import com.mcquest.server.audio.Song;
import com.mcquest.server.physics.Collider;
import com.mcquest.server.util.Debug;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.entity.damage.DamageType;
import org.jetbrains.annotations.NotNull;
import team.unnamed.hephaestus.minestom.ModelEntity;

import java.time.Duration;

public class Deer extends NonPlayerCharacter {
    private static final Component DISPLAY_NAME = Component.text("Deer", NamedTextColor.GREEN);
    private static final Vec SIZE = new Vec(1.5, 3.75, 1.5);

    private final Mmorpg mmorpg;
    private final Pos spawnPosition;
    private final Collider hitbox;
    private Entity entity;

    public Deer(Mmorpg mmorpg, Instance instance, Pos spawnPosition) {
        super(DISPLAY_NAME, 2, instance, spawnPosition);
        this.mmorpg = mmorpg;
        this.spawnPosition = spawnPosition;
        this.hitbox = new CharacterHitbox(this, instance, hitboxPosition(), SIZE);
        setHeight(SIZE.y());
        setMaxHealth(10);
        setHealth(getMaxHealth());
    }

    @Override
    public void spawn() {
        super.spawn();
        // Debug.showCollider(hitbox);
        entity = new Entity(this);
        CharacterEntityManager characterEntityManager = mmorpg.getCharacterEntityManager();
        characterEntityManager.bind(entity, this);
        entity.setInstance(getInstance(), getPosition()).join();
        mmorpg.getPhysicsManager().addCollider(hitbox);
        // entity.playAnimation("walk");
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
    public void setPosition(@NotNull Pos position) {
        super.setPosition(position);
        hitbox.setCenter(hitboxPosition());
    }

    private Pos hitboxPosition() {
        return getPosition().add(0.0, SIZE.y() / 2.0, 0.0);
    }

    @Override
    public void damage(@NotNull DamageSource source, double amount) {
        if (source instanceof PlayerCharacter pc) {
            PlayerCharacterMusicPlayer musicPlayer = pc.getMusicPlayer();
            Song song = musicPlayer.getSong();
            if (song == Music.DUNGEON) {
                musicPlayer.setSong(null);
            } else {
                musicPlayer.setSong(Music.DUNGEON);
            }
        }
        super.damage(source, amount);
        if (isAlive()) {
            entity.damage(DamageType.VOID, 0f);
        } else {
            if (source instanceof PlayerCharacter pc) {
                pc.grantExperiencePoints(50);
            }
            mmorpg.getSchedulerManager().buildTask(this::respawn).delay(Duration.ofSeconds(5)).schedule();
        }
    }

    private void respawn() {
        setHealth(getMaxHealth());
    }

    @Override
    public Attitude getAttitude(@NotNull Character character) {
        return Attitude.HOSTILE;
    }

    public static class Entity extends ModelEntity {
        private final Deer deer;

        private Entity(Deer deer) {
            super(Models.UNDEAD_KNIGHT);
            this.deer = deer;
            getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.05f);
            addAIGroup(new EntityAIGroupBuilder()
                    .addGoalSelector(new RandomStrollGoal(this, 10))
                    .build());
        }

        @Override
        public void tick(long time) {
            super.tick(time);
            if (deer.isSpawned()) {
                deer.setPosition(getPosition());
            }
        }
    }
}
