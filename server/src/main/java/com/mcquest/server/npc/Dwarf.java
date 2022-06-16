package com.mcquest.server.npc;

import com.mcquest.server.character.CharacterEntityManager;
import com.mcquest.server.character.NonPlayerCharacter;
import com.mcquest.server.util.ResourceLoader;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.ai.EntityAIGroupBuilder;
import net.minestom.server.entity.ai.goal.RandomStrollGoal;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.instance.Instance;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.minestom.ModelEntity;
import team.unnamed.hephaestus.reader.ModelReader;
import team.unnamed.hephaestus.reader.blockbench.BBModelReader;

import java.io.IOException;

public class Dwarf extends NonPlayerCharacter {
    private static final Component DISPLAY_NAME = Component.text("Dwarf", NamedTextColor.GREEN);
    private static final Model MODEL;

    private final Pos spawnPosition;
    private Entity entity;

    static {
        ModelReader reader = BBModelReader.blockbench();
        try {
            MODEL = reader.read(ResourceLoader.getResourceAsStream("models/dwarf.bbmodel"));
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public Dwarf(Instance instance, Pos spawnPosition) {
        super(DISPLAY_NAME, 25, instance, spawnPosition);
        this.spawnPosition = spawnPosition;
    }

    @Override
    public void spawn() {
        super.spawn();
        entity = new Entity(this);
        CharacterEntityManager.register(entity, this);
        entity.setInstance(getInstance(), getPosition());
        MinecraftServer.getGlobalEventHandler().addListener(PlayerBlockInteractEvent.class, event -> {
            System.out.println(MODEL.animations().get("walk"));
            entity.animationController().queue(MODEL.animations().get("walk"));
        });
    }

    @Override
    public void despawn() {
        super.despawn();
        CharacterEntityManager.unregister(entity);
        entity.remove();
        setPosition(spawnPosition);
    }

    public static class Entity extends ModelEntity {
        private final Dwarf dwarf;

        public Entity(Dwarf dwarf) {
            super(EntityType.ARMOR_STAND, MODEL);
            this.dwarf = dwarf;
            addAIGroup(new EntityAIGroupBuilder()
                    .addGoalSelector(new RandomStrollGoal(this, 5))
                    .build());
        }

        @Override
        public void tick(long time) {
            super.tick(time);
            dwarf.setPosition(getPosition());
            this.tickAnimations();
        }
    }
}
