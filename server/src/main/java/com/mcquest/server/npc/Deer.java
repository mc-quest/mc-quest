package com.mcquest.server.npc;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.Character;
import com.mcquest.server.character.*;
import com.mcquest.server.constants.Models;
import com.mcquest.server.constants.Music;
import com.mcquest.server.instance.Instance;
import com.mcquest.server.music.PlayerCharacterMusicPlayer;
import com.mcquest.server.music.Song;
import com.mcquest.server.physics.Collider;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.player.PlayerChatEvent;
import team.unnamed.hephaestus.minestom.ModelEntity;

import java.time.Duration;

public class Deer extends NonPlayerCharacter {
    private static final Component DISPLAY_NAME = Component.text("Deer", NamedTextColor.GREEN);

    private final Mmorpg mmorpg;
    private final Pos spawnPosition;
    private final Collider hitbox;
    private Entity entity;

    public Deer(Mmorpg mmorpg, Instance instance, Pos spawnPosition) {
        super(DISPLAY_NAME, 2, instance, spawnPosition);
        this.mmorpg = mmorpg;
        this.spawnPosition = spawnPosition;
        this.hitbox = new CharacterHitbox(this, instance, spawnPosition, 1, 2, 1);
        setHeight(1.5);
        setMaxHealth(100);
        setHealth(getMaxHealth());
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
        entity.damage(DamageType.VOID, 0f);
        if (!isAlive()) {
            if (source instanceof PlayerCharacter pc) {
                pc.grantExperiencePoints(50);
                mmorpg.getSchedulerManager().buildTask(this::respawn).delay(Duration.ofSeconds(5)).schedule();
            }
        }
    }

    private void respawn() {
        setHealth(getMaxHealth());
    }

    @Override
    public boolean isFriendly(Character character) {
        return false;
    }

    public static class Entity extends ModelEntity {
        private final Deer deer;

        public Entity(Deer deer) {
            super(Models.DEER);
            this.deer = deer;
            getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.05f);
            addAIGroup(new EntityAIGroupBuilder()
                    .addGoalSelector(new RandomStrollGoal(this, 10))
                    .build());
            deer.mmorpg.getGlobalEventHandler().addListener(PlayerChatEvent.class, e -> {
                playAnimation("walk");
            });
        }

        @Override
        public void tick(long time) {
            super.tick(time);
            deer.setPosition(getPosition());
        }
    }
}
