package com.mcquest.server;

import com.mcquest.server.character.NonPlayerCharacter;
import com.mcquest.server.instance.ChunkUnloader;
import com.mcquest.server.ui.InteractionManager;

public class Mmorpg {
    /**
     * Initializes the MMORPG. You must invoke MinecraftServer.init() before
     * this method.
     */
    public static void init() {
        InteractionManager.registerListeners();
        NonPlayerCharacter.startSpawner();
        ChunkUnloader.unloadVacantChunks();
    }
}
