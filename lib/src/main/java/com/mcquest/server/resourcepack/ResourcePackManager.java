package com.mcquest.server.resourcepack;

import com.mcquest.server.music.Song;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.metadata.Metadata;
import team.unnamed.creative.metadata.PackMeta;
import team.unnamed.creative.server.ResourcePackServer;
import team.unnamed.creative.sound.Sound;
import team.unnamed.creative.sound.SoundEvent;
import team.unnamed.creative.sound.SoundRegistry;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.writer.ModelWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcePackManager {
    private final Model[] models;
    private final Song[] music;
    private net.minestom.server.resourcepack.ResourcePack resourcePack;

    public ResourcePackManager(Model[] models, Song[] music) {
        this.models = models;
        this.music = music;
    }

    @ApiStatus.Internal
    public void startResourcePackServer(String address, int port) {
        try {
            ResourcePack resourcePack = ResourcePack.build(this::writeResourcePack);
            ResourcePackServer server = ResourcePackServer.builder()
                    .pack(resourcePack)
                    .address(address, port)
                    .build();
            server.start();
            this.resourcePack = net.minestom.server.resourcepack.ResourcePack
                    .forced("http://" + address + ":" + port, resourcePack.hash());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeResourcePack(FileTree tree) {
        tree.write(Metadata.builder()
                .add(PackMeta.of(9, "MMORPG resource pack"))
                .build());

        ModelWriter.resource().write(tree, List.of(models));

        Map<String, SoundEvent> sounds = new HashMap<>();
        for (Song song : music) {
            Key key = song.getSound().name();
            Writable data = Writable.inputStream(() -> song.getOgg().openStream());
            Sound.File soundFile = Sound.File.of(key, data);
            tree.write(soundFile);
            Sound sound = Sound.builder().nameSound(key).stream(true).build();
            SoundEvent soundEvent = SoundEvent.builder().sounds(sound).build();
            sounds.put(key.value(), soundEvent);
        }
        SoundRegistry soundRegistry = SoundRegistry.of("music", sounds);
        tree.write(soundRegistry);

        disableBackgroundMusic(tree);
    }

    private void disableBackgroundMusic(FileTree tree) {
        List<String> backgroundMusic = List.of(
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
        );
        Map<String, SoundEvent> sounds = new HashMap<>();
        for (String backgroundSong : backgroundMusic) {
            SoundEvent soundEvent = SoundEvent.builder().replace(true).build();
            sounds.put(backgroundSong, soundEvent);
        }
        SoundRegistry soundRegistry = SoundRegistry.of("minecraft", sounds);
        tree.write(soundRegistry);
    }

    @ApiStatus.Internal
    public net.minestom.server.resourcepack.ResourcePack getResourcePack() {
        return resourcePack;
    }
}
