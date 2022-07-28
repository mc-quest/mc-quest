package com.mcquest.server;

import com.mcquest.server.character.NonPlayerCharacterSpawner;
import com.mcquest.server.instance.ChunkUnloader;
import com.mcquest.server.ui.InteractionManager;
import net.minestom.server.MinecraftServer;

public class Mmorpg {
    private static MinecraftServer server;

    public static void init() {
        server = MinecraftServer.init();
    }

    /**
     * Starts the MMORPG.
     */
    public static void start(String address, int port) {
        if (server == null) {
            throw new IllegalStateException("not initialized");
        }
        InteractionManager.registerListeners();
        ChunkUnloader.unloadVacantChunks();
        NonPlayerCharacterSpawner.start();
        server.start(address, port);
    }
}
