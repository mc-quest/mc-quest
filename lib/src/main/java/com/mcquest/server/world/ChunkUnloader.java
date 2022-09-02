package com.mcquest.server.world;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChunkUnloadEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.ApiStatus;

public class ChunkUnloader {
    /**
     * Unloads instance chunks when they are vacated.
     */
    @ApiStatus.Internal
    public void unloadVacantChunks() {
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerChunkUnloadEvent.class, event -> {
            Instance instance = event.getPlayer().getInstance();
            int chunkX = event.getChunkX();
            int chunkZ = event.getChunkZ();
            Chunk chunk = instance.getChunk(event.getChunkX(), event.getChunkZ());
            if (chunk != null && chunk.getViewers().isEmpty()) {
                instance.unloadChunk(chunkX, chunkZ);
            }
        });
    }
}
