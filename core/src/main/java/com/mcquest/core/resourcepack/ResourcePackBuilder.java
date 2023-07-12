package com.mcquest.core.resourcepack;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.mcquest.core.playerclass.PlayerClass;
import com.mcquest.core.playerclass.Skill;
import com.mcquest.core.Mmorpg;
import com.mcquest.core.asset.Asset;
import com.mcquest.core.asset.AssetDirectory;
import com.mcquest.core.audio.AudioClip;
import com.mcquest.core.item.Item;
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
        writeMetadata(tree);
        writeGuiIcons(tree);
        writeBaseTextures(tree);
        writeSkillResources(tree);
        writeItemResources(tree);
        writeModelResources(tree);
        writeAudioResources(tree);
        disableBackgroundMusic(tree);
    }

    private void writeMetadata(FileTree tree) {
        String description = mmorpg.getName() + " resource pack";
        PackMeta packMeta = PackMeta.of(PACK_FORMAT, description);

        Metadata metadata = Metadata.builder()
                .add(packMeta)
                .build();

        tree.write(metadata);
    }

    private void writeGuiIcons(FileTree tree) {
        List<ItemOverride> overrides = new ArrayList<>();
        ResourcePackUtility.writeItemOverrides(tree, Materials.GUI, overrides);
    }

    private void writeBaseTextures(FileTree tree) {
        AssetDirectory resourcePackDir =
                new AssetDirectory(getClass().getClassLoader(), "textures");
        List<Asset> assets = resourcePackDir.getAssets();
        for (Asset asset : assets) {
            tree.write("assets/minecraft/" + asset.getPath(),
                    Writable.inputStream(asset::getStream));
        }
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

    private void writeModelResources(FileTree tree) {
        Collection<Model> models = mmorpg.getModelManager().getModels();
        ModelWriter.resource(Namespaces.MODELS).write(tree, models);
        models.forEach(Model::discardResourcePackData);
    }

    private void writeAudioResources(FileTree tree) {
        AudioClip[] audio = mmorpg.getAudioManager()
                .getAudioClips().toArray(new AudioClip[0]);
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
                "minecraft_music.json").readJson(String[].class);
        Map<String, SoundEvent> sounds = new HashMap<>();

        for (String backgroundSong : backgroundMusic) {
            SoundEvent soundEvent = SoundEvent.builder().replace(true).build();
            sounds.put(backgroundSong, soundEvent);
        }

        SoundRegistry soundRegistry = SoundRegistry.of("minecraft", sounds);
        tree.write(soundRegistry);
    }
}
