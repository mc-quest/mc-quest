package net.mcquest.core.resourcepack;

import net.mcquest.core.Mmorpg;
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
    private net.minestom.server.resourcepack.ResourcePack playerResourcePack;

    @ApiStatus.Internal
    public ResourcePackManager(Mmorpg mmorpg) {
        ResourcePackBuilder builder = new ResourcePackBuilder(mmorpg);
        resourcePack = builder.build();
    }

    @ApiStatus.Internal
    public void startServer(String address, int port) {
        try {
            server = ResourcePackServer.builder()
                    .pack(resourcePack)
                    .address(address, port)
                    .build();
            server.start();

            // TODO: URL must be updated in production.
            String resourcePackUrl = String.format("http://%s:%d#%s", address, port, resourcePack.hash());
            playerResourcePack = net.minestom.server.resourcepack.ResourcePack.forced(
                    resourcePackUrl,
                    resourcePack.hash()
            );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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

    public net.minestom.server.resourcepack.ResourcePack getPlayerResourcePack() {
        return playerResourcePack;
    }
}
