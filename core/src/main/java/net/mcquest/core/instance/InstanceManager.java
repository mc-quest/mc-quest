package net.mcquest.core.instance;

import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.Chunk;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import net.minestom.server.utils.time.Tick;
import net.minestom.server.world.biomes.Biome;
import net.minestom.server.world.biomes.BiomeManager;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.util.*;

public class InstanceManager {
    /**
     * Delay unloading chunks on the server to prevent players from rapidly
     * loading and unloading chunks by walking back and forth.
     */
    private static final Duration CHUNK_UNLOAD_DELAY = Duration.ofSeconds(3);

    private final Map<Integer, Instance> instancesById;
    /**
     * Maps chunks to remaining time until unload.
     */
    private final Map<ChunkAddress, Duration> chunksToUnload;

    @ApiStatus.Internal
    public InstanceManager(Instance[] instances, Biome[] biomes) {
        instancesById = new HashMap<>();
        for (Instance instance : instances) {
            registerInstance(instance);
        }

        chunksToUnload = new HashMap<>();

        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        scheduler.buildTask(this::tick).repeat(TaskSchedule.nextTick()).schedule();

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

    public Collection<Instance> getInstances() {
        return Collections.unmodifiableCollection(instancesById.values());
    }

    private void tick() {
        for (Instance instance : instancesById.values()) {
            Collection<Chunk> toUnloadNow = new ArrayList<>();

            for (Chunk chunk : instance.getChunks()) {
                ChunkAddress address = ChunkAddress.forChunk(chunk);
                if (chunk.getViewers().isEmpty()) {
                    if (chunksToUnload.containsKey(address)) {
                        Duration untilUnload = chunksToUnload.get(address).minus(Tick.server(1));
                        if (untilUnload.isZero()) {
                            toUnloadNow.add(chunk);
                            chunksToUnload.remove(address);
                        } else {
                            chunksToUnload.put(address, untilUnload);
                        }
                    } else {
                        chunksToUnload.put(address, CHUNK_UNLOAD_DELAY);
                    }
                } else {
                    chunksToUnload.remove(address);
                }
            }

            for (Chunk chunk : toUnloadNow) {
                instance.unloadChunk(chunk);
            }
        }
    }
}
