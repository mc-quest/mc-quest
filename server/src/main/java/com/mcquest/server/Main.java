package com.mcquest.server;

import com.mcquest.server.character.NonPlayerCharacterSpawner;
import com.mcquest.server.constants.Instances;
import com.mcquest.server.load.InstanceLoader;
import com.mcquest.server.load.ItemLoader;
import com.mcquest.server.load.PlayerClassLoader;
import com.mcquest.server.load.QuestLoader;
import com.mcquest.server.npc.Broodling;
import com.mcquest.server.npc.Rabbit;
import com.mcquest.server.npc.Wolf;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.metadata.animal.RabbitMeta;

public class Main {
    private static final String SERVER_ADDRESS = "0.0.0.0";
    private static final int SERVER_PORT = 25565;

    public static void main(String[] args) {
        MinecraftServer server = MinecraftServer.init();
        load();
        Lobby.createLobby();
        NonPlayerCharacterSpawner.add(new Rabbit(Instances.ELADRADOR, new Pos(0, 70, 0), RabbitMeta.Type.BLACK));
        NonPlayerCharacterSpawner.add(new Wolf(Instances.ELADRADOR, new Pos(5, 70, 5)));
        NonPlayerCharacterSpawner.add(new Broodling(Instances.ELADRADOR, new Pos(-5, 70, 5)));
        server.start(SERVER_ADDRESS, SERVER_PORT);
        Mmorpg.start();
    }

    private static void load() {
        InstanceLoader.loadInstances();
        PlayerClassLoader.loadPlayerClasses();
        ItemLoader.loadItems();
        QuestLoader.loadQuests();
    }
}
