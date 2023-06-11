package com.mcquest.server.resourcepack;

import com.mcquest.server.asset.Asset;
import com.mcquest.server.audio.AudioClip;
import com.mcquest.server.item.Item;
import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.playerclass.Skill;
import net.kyori.adventure.key.Key;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.ItemOverride;
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
    private final Model[] models;
    private final AudioClip[] audio;

    ResourcePackBuilder(Consumer<FileTree> baseWriter, PlayerClass[] playerClasses,
                        Item[] items, Model[] models, AudioClip[] audio) {
        this.baseWriter = baseWriter;
        this.playerClasses = playerClasses;
        this.items = items;
        this.models = models;
        this.audio = audio;
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
        writeModelResources(tree);
        writeAudioResources(tree);
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

    private void writeModelResources(FileTree tree) {
        ModelWriter.resource(Namespaces.MODELS).write(tree, List.of(models));
    }

    private void writeAudioResources(FileTree tree) {
        Map<String, SoundEvent> sounds = new HashMap<>();
        for (int i = 0; i < audio.length; i++) {
            AudioClip audioClip = audio[i];
            int id = i + 1;
            audioClip.writeResources(tree, id, sounds);
        }
        SoundRegistry soundRegistry = SoundRegistry.of(Namespaces.AUDIO, sounds);
        tree.write(soundRegistry);
    }

    private void disableBackgroundMusic(FileTree tree) {
        String[] backgroundMusic = new Asset(ResourcePackBuilder.class.getClassLoader(),
                "MinecraftMusic.json").readJson(String[].class);
        Map<String, SoundEvent> sounds = new HashMap<>();
        for (String backgroundSong : backgroundMusic) {
            SoundEvent soundEvent = SoundEvent.builder().replace(true).build();
            sounds.put(backgroundSong, soundEvent);
        }
        SoundRegistry soundRegistry = SoundRegistry.of("minecraft", sounds);
        tree.write(soundRegistry);
    }
}
