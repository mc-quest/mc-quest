package com.mcquest.server;

import com.mcquest.server.character.NonPlayerCharacter;
import com.mcquest.server.instance.ChunkUnloader;
import com.mcquest.server.ui.InteractionManager;

public class Mmorpg {
    /**
     * Starts the MMORPG.
     */
    public static void start() {
        InteractionManager.registerListeners();
        ChunkUnloader.unloadVacantChunks();
        NonPlayerCharacter.startSpawner();
    }
}
