package com.mcquest.server;

import com.mcquest.server.character.NonPlayerCharacterSpawner;
import com.mcquest.server.instance.ChunkUnloader;
import com.mcquest.server.ui.InteractionHandler;
import net.minestom.server.MinecraftServer;

public class Mmorpg {

    private static MinecraftServer server;

    public static void init() {
        server = MinecraftServer.init();
        InteractionHandler.registerListeners();
        ChunkUnloader.unloadVacantChunks();
    }

    public static void start(String address, int port) {
        if (server == null) {
            throw new IllegalStateException("not initialized");
        }
        NonPlayerCharacterSpawner.start();
        server.start(address, port);
    }
}
