package com.mcquest.server.instance;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChunkUnloadEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.InstanceContainer;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public class InstanceManager {
    private final Map<Integer, InstanceContainer> instancesById;

    @ApiStatus.Internal
    public InstanceManager(Instance[] instances) {
        instancesById = new HashMap<>();
        for (Instance instance : instances) {
            registerInstance(instance);
        }
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerChunkUnloadEvent.class, this::unloadVacantChunk);
    }

    private void registerInstance(Instance instance) {
        int id = instance.getId();
        if (instancesById.containsKey(id)) {
            throw new IllegalArgumentException("id already used: " + id);
        }
        instancesById.put(id, instance);
        MinecraftServer.getInstanceManager().registerInstance(instance);
    }

    public InstanceContainer getInstance(int id) {
        return instancesById.get(id);
    }

    private void unloadVacantChunk(PlayerChunkUnloadEvent event) {
        Instance instance = (Instance) event.getInstance();
        int chunkX = event.getChunkX();
        int chunkZ = event.getChunkZ();
        Chunk chunk = instance.getChunk(event.getChunkX(), event.getChunkZ());
        if (chunk != null && chunk.getViewers().isEmpty()) {
            instance.unloadChunk(chunkX, chunkZ);
        }
    }
}
