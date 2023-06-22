package com.mcquest.core.resourcepack;

import com.mcquest.core.Mmorpg;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.server.ResourcePackServer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

public class ResourcePackManager {
    private final ResourcePack resourcePack;
    private ResourcePackServer server;
    private String resourcePackUrl;

    @ApiStatus.Internal
    public ResourcePackManager(Mmorpg mmorpg) {
        ResourcePackBuilder builder = new ResourcePackBuilder(mmorpg);
        resourcePack = builder.build();
    }

    @ApiStatus.Internal
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
     * Writes the server resource pack to the specified file for debugging.
     */
    public void writeResourcePack(File file) {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(resourcePack.bytes());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
