package com.mcquest.server.instance;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChunkUnloadEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.world.biomes.Biome;
import net.minestom.server.world.biomes.BiomeManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

public class InstanceManager {
    private final Map<Integer, Instance> instancesById;

    @ApiStatus.Internal
    public InstanceManager(Instance[] instances, Biome[] biomes) {
        instancesById = new HashMap<>();
        for (Instance instance : instances) {
            registerInstance(instance);
        }
        BiomeManager biomeManager = MinecraftServer.getBiomeManager();
        for (Biome biome : biomes) {
            biomeManager.addBiome(biome);
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

    public Instance getInstance(int id) {
        return instancesById.get(id);
    }

    private void unloadVacantChunk(PlayerChunkUnloadEvent event) {
        Instance instance = (Instance) event.getInstance();
        int chunkX = event.getChunkX();
        int chunkZ = event.getChunkZ();
        Chunk chunk = instance.getChunk(chunkX, chunkX);
        if (chunk != null && chunk.getViewers().isEmpty()
                && instance.isChunkLoaded(chunkX, chunkZ)) {
            instance.unloadChunk(chunkX, chunkZ);
        }
    }
}
