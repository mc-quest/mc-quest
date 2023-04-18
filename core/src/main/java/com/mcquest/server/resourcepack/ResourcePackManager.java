package com.mcquest.server.resourcepack;

import com.mcquest.server.item.Item;
import com.mcquest.server.music.Song;
import com.mcquest.server.playerclass.PlayerClass;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.server.ResourcePackServer;
import team.unnamed.hephaestus.Model;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;

@ApiStatus.Internal
public class ResourcePackManager {
    private final ResourcePack resourcePack;
    private ResourcePackServer server;
    private String resourcePackUrl;

    public ResourcePackManager(Consumer<FileTree> baseWriter, PlayerClass[] playerClasses,
                               Item[] items, Song[] music, Model[] models) {
        ResourcePackBuilder builder = new ResourcePackBuilder(
                baseWriter, playerClasses, items, music, models);
        resourcePack = builder.build();
    }

    public void startServer(String address, int port) {
        try {
            resourcePackUrl = String.format("http://%s:%d", address, port);
            server = ResourcePackServer.builder()
                    .pack(resourcePack)
                    .address(address, port)
                    .build();
            server.start();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public String getResourcePackHash() {
        return resourcePack.hash();
    }

    public String getResourcePackUrl() {
        return resourcePackUrl;
    }

    /**
     * Writes the server resource pack to the specified file. Used only for debugging.
     */
    public void writeResourcePack(File file) {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(resourcePack.bytes());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
