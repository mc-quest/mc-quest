package com.mcquest.server;

import com.mcquest.server.character.NonPlayerCharacter;
import com.mcquest.server.ui.InteractionManager;
import net.minestom.server.MinecraftServer;

public class MmorpgServer {
    private final String address;
    private final int port;
    private final MinecraftServer minecraftServer;

    public MmorpgServer(String address, int port) {
        this.address = address;
        this.port = port;
        minecraftServer = MinecraftServer.init();
        InteractionManager.registerListeners();
        NonPlayerCharacter.startSpawner();
    }

    public void start() {
        minecraftServer.start(address, port);
    }
}
