package com.mcquest.server;

import com.mcquest.server.load.ItemLoader;
import com.mcquest.server.load.PlayerClassLoader;
import com.mcquest.server.load.QuestLoader;
import com.mcquest.server.npc.Dwarf;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;

public class Main {
    private static final String SERVER_ADDRESS = "0.0.0.0";
    private static final int SERVER_PORT = 25565;

    public static void main(String[] args) {
        MmorpgServer server = new MmorpgServer(SERVER_ADDRESS, SERVER_PORT);
        // load();
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();
        instanceContainer.setGenerator(unit -> {
            unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK);
        });
        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(PlayerLoginEvent.class, event -> {
            Player player = event.getPlayer();
            player.setGameMode(GameMode.ADVENTURE);
            event.setSpawningInstance(instanceContainer);
            player.setRespawnPoint(new Pos(0, 40, 0));
        });

        new Dwarf(instanceContainer, new Pos(0, 40, 0));
        server.start();
    }

    private static void load() {
        PlayerClassLoader.loadPlayerClasses();
        ItemLoader.loadItems();
        QuestLoader.loadQuests();
    }
}
