package net.mcquest.core.resourcepack;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.asset.Asset;
import net.mcquest.core.asset.AssetDirectory;
import net.mcquest.core.audio.AudioClip;
import net.mcquest.core.item.Item;
import net.mcquest.core.music.Song;
import net.mcquest.core.playerclass.PlayerClass;
import net.mcquest.core.playerclass.Skill;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.Material;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.metadata.Metadata;
import team.unnamed.creative.metadata.PackMeta;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.sound.SoundEvent;
import team.unnamed.creative.sound.SoundRegistry;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.writer.ModelWriter;

import java.util.*;

class ResourcePackBuilder {
    private static final int PACK_FORMAT = 9;

    private final Mmorpg mmorpg;

    ResourcePackBuilder(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
    }

    ResourcePack build() {
        return ResourcePack.build(this::writeResources);
    }

    private void writeResources(FileTree tree) {
        writeBaseResourcePack(tree);
        writeMetadata(tree);
        writeTextures(tree);
        writeSkillResources(tree);
        writeItemResources(tree);
        writeMusicResources(tree);
        writeModelResources(tree);
        writeAudioResources(tree);
        disableBackgroundMusic(tree);
    }

    private void writeBaseResourcePack(FileTree tree) {
        String path = "resourcepack";
        AssetDirectory resourcePackDir = AssetDirectory.of(classLoader(), path);
        for (Asset asset : resourcePackDir.getAssets()) {
            String subPath = asset.getPath().substring(path.length() + 1);
            tree.write(subPath, Writable.inputStream(asset::getStream));
        }
    }

    private void writeMetadata(FileTree tree) {
        String description = mmorpg.getName() + " resource pack";
        PackMeta packMeta = PackMeta.of(PACK_FORMAT, description);

        Metadata metadata = Metadata.builder()
                .add(packMeta)
                .build();

        tree.write(metadata);
    }

    private void writeTextures(FileTree tree) {
        String[] icons = {
                "hotbar_skill_placeholder",
                "hotbar_skill_placeholder_flashing",
                "hotbar_consumable_placeholder",
                "hotbar_consumable_placeholder_flashing"
        };

        List<ItemOverride> overrides = new ArrayList<>();

        for (String icon : icons) {
            Asset asset = Asset.of(classLoader(), String.format("textures/%s.png", icon));
            Key key = Key.key(Namespaces.GUI, icon);
            ResourcePackUtility.writeIcon(tree, asset, key, overrides);
        }

        ResourcePackUtility.writeItemOverrides(tree, Materials.GUI, overrides);
    }

    private void writeSkillResources(FileTree tree) {
        Collection<PlayerClass> playerClasses = mmorpg.getPlayerClassManager()
                .getPlayerClasses();
        List<ItemOverride> overrides = new ArrayList<>();

        for (PlayerClass playerClass : playerClasses) {
            for (Skill skill : playerClass.getSkills()) {
                skill.writeResources(tree, overrides);
            }
        }

        ResourcePackUtility.writeItemOverrides(tree, Materials.SKILL, overrides);
    }

    private void writeItemResources(FileTree tree) {
        Collection<Item> items = mmorpg.getItemManager().getItems();
        ListMultimap<Material, ItemOverride> overrides = ArrayListMultimap.create();

        for (Item item : items) {
            item.writeResources(tree, overrides);
        }

        ResourcePackUtility.writeItemOverrides(tree, overrides);
    }

    private void writeMusicResources(FileTree tree) {
        Collection<Song> music = mmorpg.getMusicManager().getMusic();
        Map<String, SoundEvent> sounds = new HashMap<>();

        for (Song song : music) {
            song.writeResources(tree, sounds);
        }

        SoundRegistry soundRegistry = SoundRegistry.of(Namespaces.MUSIC, sounds);
        tree.write(soundRegistry);
    }

    private void writeModelResources(FileTree tree) {
        Collection<Model> models = mmorpg.getModelManager().getModels();
        ModelWriter.resource(Namespaces.MODELS).write(tree, models);
        models.forEach(Model::discardResourcePackData);
    }

    private void writeAudioResources(FileTree tree) {
        Collection<AudioClip> audioClips = mmorpg.getAudioManager().getAudioClips();
        Map<String, SoundEvent> sounds = new HashMap<>();

        int id = 1;
        for (AudioClip audioClip : audioClips) {
            audioClip.writeResources(tree, id, sounds);
            id++;
        }

        SoundRegistry soundRegistry = SoundRegistry.of(Namespaces.AUDIO, sounds);
        tree.write(soundRegistry);
    }

    private void disableBackgroundMusic(FileTree tree) {
        String[] backgroundMusic = Asset.of(classLoader(), "data/minecraft_music.json")
                .readJson(String[].class);
        Map<String, SoundEvent> sounds = new HashMap<>();

        for (String backgroundSong : backgroundMusic) {
            SoundEvent soundEvent = SoundEvent.builder().replace(true).build();
            sounds.put(backgroundSong, soundEvent);
        }

        SoundRegistry soundRegistry = SoundRegistry.of("minecraft", sounds);
        tree.write(soundRegistry);
    }

    private ClassLoader classLoader() {
        return getClass().getClassLoader();
    }
}
