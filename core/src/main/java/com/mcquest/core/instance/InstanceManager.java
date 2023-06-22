package com.mcquest.core.instance;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChunkLoadEvent;
import net.minestom.server.event.player.PlayerChunkUnloadEvent;
import net.minestom.server.instance.Chunk;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import net.minestom.server.world.biomes.Biome;
import net.minestom.server.world.biomes.BiomeManager;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InstanceManager {
    /**
     * Delay unloading chunks on the server to prevent players from rapidly
     * loading and unloading chunks by walking back and forth.
     */
    private static final Duration CHUNK_UNLOAD_DELAY = Duration.ofSeconds(3);

    private final Map<Integer, Instance> instancesById;
    private final Map<ChunkAddress, Task> chunkUnloadTasks;

    @ApiStatus.Internal
    public InstanceManager(Instance[] instances, Biome[] biomes) {
        instancesById = new HashMap<>();
        for (Instance instance : instances) {
            registerInstance(instance);
        }

        chunkUnloadTasks = new HashMap<>();
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerChunkLoadEvent.class, this::handlePlayerChunkLoad);
        eventHandler.addListener(PlayerChunkUnloadEvent.class, this::handlePlayerChunkUnload);

        BiomeManager biomeManager = MinecraftServer.getBiomeManager();
        for (Biome biome : biomes) {
            biomeManager.addBiome(biome);
        }
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

    private void handlePlayerChunkLoad(PlayerChunkLoadEvent event) {
        Instance instance = (Instance) event.getInstance();
        int x = event.getChunkX();
        int z = event.getChunkZ();
        ChunkAddress chunkAddress = new ChunkAddress(instance, x, z);

        if (chunkUnloadTasks.containsKey(chunkAddress)) {
            chunkUnloadTasks.remove(chunkAddress).cancel();
        }
    }

    private void handlePlayerChunkUnload(PlayerChunkUnloadEvent event) {
        Player player = event.getPlayer();
        Instance instance = (Instance) event.getInstance();
        int x = event.getChunkX();
        int z = event.getChunkZ();
        ChunkAddress chunkAddress = new ChunkAddress(instance, x, z);

        // Duplicate PlayerChunkUnloadEvents can be fired when a player
        // leaves a chunk and changes instances, so check if we already
        // scheduled unloading.
        if (chunkUnloadTasks.containsKey(chunkAddress)) {
            return;
        }

        Chunk chunk = instance.getChunk(x, z);
        if (chunk == null) {
            return;
        }

        Set<Player> viewers = chunk.getViewers();
        if (viewers.isEmpty() || viewers.equals(Set.of(player))) {
            SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
            Task unloadTask = scheduler.buildTask(() -> instance.unloadChunk(x, z))
                    .delay(CHUNK_UNLOAD_DELAY)
                    .schedule();
            chunkUnloadTasks.put(chunkAddress, unloadTask);
        }
    }
}
