package com.mcquest.server.instance;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChunkUnloadEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public class InstanceManager {
    private final Map<String, Instance> instances;

    @ApiStatus.Internal
    public InstanceManager() {
        this.instances = new HashMap<>();
    }

    public InstanceContainer createInstanceContainer(String name) {
        if (instances.containsKey(name)) {
            throw new IllegalArgumentException("name already used: " + name);
        }
        InstanceContainer instance = MinecraftServer.getInstanceManager().createInstanceContainer();
        instances.put(name, instance);
        return instance;
    }

    public Instance getInstance(String name) {
        return instances.get(name);
    }

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
