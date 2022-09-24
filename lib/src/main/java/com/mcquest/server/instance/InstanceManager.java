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
    private final Map<Integer, InstanceContainer> instances;

    @ApiStatus.Internal
    public InstanceManager() {
        this.instances = new HashMap<>();
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerChunkUnloadEvent.class, this::unloadVacantChunk);
    }

    public InstanceContainer createInstance(int id) {
        if (instances.containsKey(id)) {
            throw new IllegalArgumentException("id already used: " + id);
        }
        InstanceContainer instance = MinecraftServer.getInstanceManager().createInstanceContainer();
        instances.put(id, instance);
        return instance;
    }

    public InstanceContainer getInstance(int id) {
        return instances.get(id);
    }

    public Integer idOf(Instance instance) {
        for (Map.Entry<Integer, InstanceContainer> entry : instances.entrySet()) {
            if (entry.getValue() == instance) {
                return entry.getKey();
            }
        }
        return null;
    }

    private void unloadVacantChunk(PlayerChunkUnloadEvent event) {
        Instance instance = event.getPlayer().getInstance();
        int chunkX = event.getChunkX();
        int chunkZ = event.getChunkZ();
        Chunk chunk = instance.getChunk(event.getChunkX(), event.getChunkZ());
        if (chunk != null && chunk.getViewers().isEmpty()) {
            instance.unloadChunk(chunkX, chunkZ);
        }
    }
}
