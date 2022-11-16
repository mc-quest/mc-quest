package com.mcquest.server.resource;

import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.metadata.Metadata;
import team.unnamed.creative.metadata.PackMeta;
import team.unnamed.creative.server.ResourcePackServer;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.writer.ModelWriter;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class ResourceManager {
    private final Collection<Model> models;
    private net.minestom.server.resourcepack.ResourcePack resourcePack;

    public ResourceManager(Model[] models) {
        this.models = List.of(models);
    }

    @ApiStatus.Internal
    public void startResourcePackServer(String address, int port) {
        ResourcePack resourcePack = ResourcePack.build(tree -> {
            tree.write(Metadata.builder()
                    .add(PackMeta.of(9, "MCQuest resource pack"))
                    .build());
            ModelWriter.resource().write(tree, models);
        });
        ResourcePackServer server;
        try {
            server = ResourcePackServer.builder()
                    .pack(resourcePack)
                    .address(address, port)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.resourcePack = net.minestom.server.resourcepack.ResourcePack
                .forced("http://" + address + ":" + port, resourcePack.hash());
        server.start();
    }

    @ApiStatus.Internal
    public net.minestom.server.resourcepack.ResourcePack getResourcePack() {
        return resourcePack;
    }
}
