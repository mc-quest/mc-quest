package com.mcquest.server;

import com.mcquest.server.load.InstanceLoader;
import com.mcquest.server.load.ItemLoader;
import com.mcquest.server.load.PlayerClassLoader;
import com.mcquest.server.load.QuestLoader;
import net.minestom.server.MinecraftServer;

public class Main {
    private static final String SERVER_ADDRESS = "0.0.0.0";
    private static final int SERVER_PORT = 25565;

    public static void main(String[] args) {
        Mmorpg.init();
        load();
        Lobby.createLobby();
        Mmorpg.start(SERVER_ADDRESS, SERVER_PORT);
    }

    private static void load() {
        InstanceLoader.loadInstances();
        PlayerClassLoader.loadPlayerClasses();
        ItemLoader.loadItems();
        QuestLoader.loadQuests();
    }
}
