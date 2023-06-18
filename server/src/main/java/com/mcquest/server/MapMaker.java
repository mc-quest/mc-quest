package com.mcquest.server;

import com.mcquest.server.instance.Instance;
import net.minestom.server.instance.Chunk;
import net.minestom.server.instance.block.Block;
import net.minestom.server.item.Material;
import net.minestom.server.utils.chunk.ChunkUtils;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MapMaker {
    private static final Map<Material, Integer> MAP_COLOR_BY_MATERIAL = new HashMap<>();

    public static BufferedImage createMap(Instance instance, int minX, int minZ,
                                          int maxX, int maxZ, int maxY) {
        int width = maxX - minX + 1;
        int height = maxZ - minZ + 1;
        BufferedImage map = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Collection<Chunk> toUnload = new ArrayList<>();

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int chunkX = ChunkUtils.getChunkCoordinate(x);
                int chunkZ = ChunkUtils.getChunkCoordinate(z);
                if (!instance.isChunkLoaded(chunkX, chunkZ)) {
                    Chunk chunk = instance.loadChunk(chunkX, chunkZ).join();
                    toUnload.add(chunk);
                }
                for (int y = maxY; y >= 0; y--) {
                    Block block = instance.getBlock(x, y, z);
                    if (!block.isAir()) {
                        int color = mapColor(block);
                        map.setRGB(x - minX, z - minZ, color);
                        break;
                    }
                }
            }
        }

        for (Chunk chunk : toUnload) {
            instance.unloadChunk(chunk);
        }

        return map;
    }

    private static int mapColor(Block block) {
        if (block.compare(Block.GRASS_BLOCK) || block.compare(Block.GRASS) || block.compare(Block.TALL_GRASS)) {
            return 0x00ff00;
        } else if (block.compare(Block.WATER)) {
            return 0x0000ff;
        } else if (true) {
            return 0x000000;
        }
        Material material = block.registry().material();
        if (!MAP_COLOR_BY_MATERIAL.containsKey(material)) {
            throw new IllegalArgumentException("Unknown material: " + material.key());
        }
        return MAP_COLOR_BY_MATERIAL.get(material);
    }
}
