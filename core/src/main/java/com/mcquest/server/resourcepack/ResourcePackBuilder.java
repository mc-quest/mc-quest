package com.mcquest.server.resourcepack;

import com.mcquest.server.item.Item;
import com.mcquest.server.music.Song;
import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.playerclass.Skill;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.sound.Sound;
import team.unnamed.creative.sound.SoundEvent;
import team.unnamed.creative.sound.SoundRegistry;
import team.unnamed.creative.texture.Texture;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.writer.ModelWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

class ResourcePackBuilder {
    private final Consumer<FileTree> baseWriter;
    private final PlayerClass[] playerClasses;
    private final Item[] items;
    private final Song[] music;
    private final Model[] models;

    ResourcePackBuilder(Consumer<FileTree> baseWriter, PlayerClass[] playerClasses,
                        Item[] items, Song[] music, Model[] models) {
        this.baseWriter = baseWriter;
        this.playerClasses = playerClasses;
        this.items = items;
        this.music = music;
        this.models = models;
    }

    ResourcePack build() {
        return ResourcePack.build(this::writeResources);
    }

    private void writeResources(FileTree tree) {
        writeBaseResourcePack(tree);
        if (!tree.exists("pack.mcmeta")) {
            throw new IllegalArgumentException("Resource pack writer must write pack.mcmeta");
        }
        writeGuiTextures(tree);
        writeSkillResources(tree);
        writeItemResources(tree);
        writeMusicResources(tree);
        writeModelResources(tree);
        disableBackgroundMusic(tree);
    }

    private void writeBaseResourcePack(FileTree tree) {
        baseWriter.accept(tree);
    }

    private void writeGuiTextures(FileTree tree) {
        Texture texture = Texture.builder()
                .key(Key.key("gui/icons"))
                .data(Writable.resource(getClass().getClassLoader(), "textures/icons.png"))
                .build();
        tree.write(texture);
    }

    private void writeSkillResources(FileTree tree) {
        int customModelData = 0;
        for (PlayerClass playerClass : playerClasses) {
            for (Skill skill : playerClass.getSkills()) {
                customModelData += skill.writeResources(tree, customModelData);
            }
        }
    }

    private void writeItemResources(FileTree tree) {
        int customModelData = 0;
        List<ItemOverride> overrides = new ArrayList<>();
        for (Item item : items) {
            customModelData += item.writeResources(tree, customModelData, overrides);
        }
        ResourcePackUtility.writeItemOverrides(tree, Item.ITEM_MATERIAL, overrides);
    }

    private void writeMusicResources(FileTree tree) {
        Map<String, SoundEvent> sounds = new HashMap<>();
        for (Song song : music) {
            Key key = song.getSound().name();
            Writable data = Writable.inputStream(song.getAudio()::getStream);
            Sound.File soundFile = Sound.File.of(key, data);
            tree.write(soundFile);
            Sound sound = Sound.builder().nameSound(key).stream(true).build();
            SoundEvent soundEvent = SoundEvent.builder().sounds(sound).build();
            sounds.put(key.value(), soundEvent);
        }
        SoundRegistry soundRegistry = SoundRegistry.of("music", sounds);
        tree.write(soundRegistry);
    }

    private void writeModelResources(FileTree tree) {
        ModelWriter.resource().write(tree, List.of(models));
    }

    private void disableBackgroundMusic(FileTree tree) {
        String[] backgroundMusic = {
                "music.game",
                "music.creative",
                "music.under_water",
                "music.menu",
                "music.credits",
                "music.nether.basalt_deltas",
                "music.nether.nether_wastes",
                "music.nether.soul_sand_valley",
                "music.nether.crimson_forest",
                "music.nether.crimson_forest",
                "music.nether.nether_wastes",
                "music.nether.basalt_deltas",
                "music.nether.soul_sand_valley",
                "music.dragon",
                "music.end"
        };
        Map<String, SoundEvent> sounds = new HashMap<>();
        for (String backgroundSong : backgroundMusic) {
            SoundEvent soundEvent = SoundEvent.builder().replace(true).build();
            sounds.put(backgroundSong, soundEvent);
        }
        SoundRegistry soundRegistry = SoundRegistry.of("minecraft", sounds);
        tree.write(soundRegistry);
    }
}
