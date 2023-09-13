package com.mcquest.core.resourcepack;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.mcquest.core.Mmorpg;
import com.mcquest.core.asset.Asset;
import com.mcquest.core.asset.AssetDirectory;
import com.mcquest.core.audio.AudioClip;
import com.mcquest.core.item.Item;
import com.mcquest.core.music.Song;
import com.mcquest.core.playerclass.PlayerClass;
import com.mcquest.core.playerclass.Skill;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.Material;
import team.unnamed.creative.BuiltResourcePack;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.serialize.minecraft.MinecraftResourcePackWriter;
import team.unnamed.creative.sound.SoundEvent;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.writer.ModelWriter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class ResourcePackBuilder {
    private static final int PACK_FORMAT = 9;

    private final Mmorpg mmorpg;

    ResourcePackBuilder(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
    }

    BuiltResourcePack build() {
        ResourcePack resourcePack = ResourcePack.create();
        writeBaseResourcePack(resourcePack);
        writePackMeta(resourcePack);
        writeCoreIcons(resourcePack);
        writeSkillResources(resourcePack);
        writeItemResources(resourcePack);
        writeMusicResources(resourcePack);
        writeModelResources(resourcePack);
        writeAudioResources(resourcePack);
        disableBackgroundMusic(resourcePack);
        return MinecraftResourcePackWriter.minecraft().build(resourcePack);
    }

    private void writeBaseResourcePack(ResourcePack resourcePack) {
        String basePath = "resourcepack";
        AssetDirectory resourcePackDir = AssetDirectory.of(classLoader(), basePath);
        for (Asset asset : resourcePackDir.getAssets()) {
            String subPath = asset.getPath().substring(basePath.length() + 1);
            resourcePack.unknownFile(subPath, Writable.inputStream(asset::getStream));
        }
    }

    private void writePackMeta(ResourcePack resourcePack) {
        resourcePack.packMeta(
                PACK_FORMAT,
                String.format("%s resource pack", mmorpg.getName())
        );
    }

    private void writeCoreIcons(ResourcePack resourcePack) {
        String[] icons = {
                "hotbar_skill_placeholder",
                "hotbar_skill_placeholder_flashing",
                "hotbar_consumable_placeholder",
                "hotbar_consumable_placeholder_flashing"
        };

        List<ItemOverride> overrides = new ArrayList<>();

        for (String icon : icons) {
            Asset asset = Asset.of(
                    classLoader(),
                    String.format("icons/%s.png", icon)
            );
            Key key = Key.key(Namespaces.GUI, icon);
            ResourcePackUtility.writeIcon(resourcePack, asset, key, overrides);
        }

        ResourcePackUtility.writeItemOverrides(
                resourcePack,
                Materials.GUI,
                overrides
        );
    }

    private void writeSkillResources(ResourcePack resourcePack) {
        Collection<PlayerClass> playerClasses = mmorpg.getPlayerClassManager()
                .getPlayerClasses();
        List<ItemOverride> overrides = new ArrayList<>();

        for (PlayerClass playerClass : playerClasses) {
            for (Skill skill : playerClass.getSkills()) {
                skill.writeResources(resourcePack, overrides);
            }
        }

        ResourcePackUtility.writeItemOverrides(resourcePack, Materials.SKILL, overrides);
    }

    private void writeItemResources(ResourcePack resourcePack) {
        Collection<Item> items = mmorpg.getItemManager().getItems();
        ListMultimap<Material, ItemOverride> overrides = ArrayListMultimap.create();

        for (Item item : items) {
            item.writeResources(resourcePack, overrides);
        }

        ResourcePackUtility.writeItemOverrides(resourcePack, overrides);
    }

    private void writeMusicResources(ResourcePack resourcePack) {
        Collection<Song> music = mmorpg.getMusicManager().getMusic();
        for (Song song : music) {
            song.writeResources(resourcePack);
        }
    }

    private void writeModelResources(ResourcePack resourcePack) {
        Collection<Model> models = mmorpg.getModelManager().getModels();
        ModelWriter.resource(Namespaces.MODELS).write(resourcePack, models);
        models.forEach(Model::discardResourcePackData);
    }

    private void writeAudioResources(ResourcePack resourcePack) {
        Collection<AudioClip> audioClips = mmorpg.getAudioManager().getAudioClips();
        int id = 1;
        for (AudioClip audioClip : audioClips) {
            audioClip.writeResources(resourcePack, id);
            id++;
        }
    }

    private void disableBackgroundMusic(ResourcePack resourcePack) {
        String[] backgroundMusic = Asset.of(
                classLoader(),
                "data/minecraft_music.json"
        ).readJson(String[].class);

        for (String backgroundSong : backgroundMusic) {
            SoundEvent soundEvent = SoundEvent.builder()
                    .key(Key.key(Namespaces.MINECRAFT, backgroundSong))
                    .replace(true)
                    .build();
            resourcePack.soundEvent(soundEvent);
        }
    }

    private ClassLoader classLoader() {
        return getClass().getClassLoader();
    }
}
