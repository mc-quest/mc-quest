package com.mcquest.server.resourcepack;

import com.mcquest.server.item.Item;
import com.mcquest.server.music.Song;
import com.mcquest.server.playerclass.Skill;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.server.ResourcePackServer;
import team.unnamed.hephaestus.Model;

import java.io.*;
import java.util.function.Consumer;

@ApiStatus.Internal
public class ResourcePackManager {
    private final ResourcePack resourcePack;
    private ResourcePackServer server;
    private String resourcePackUrl;

    public ResourcePackManager(Consumer<FileTree> baseWriter, Skill[] skills,
                               Item[] items, Song[] music, Model[] models) {
        ResourcePackBuilder builder = new ResourcePackBuilder(
                baseWriter, skills, items, music, models);
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
            throw new RuntimeException(e);
        }
    }

    public String getResourcePackHash() {
        return resourcePack.hash();
    }

    public String getResourcePackUrl() {
        return resourcePackUrl;
    }
}
