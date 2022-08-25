package com.mcquest.server;

import com.mcquest.server.character.NonPlayerCharacterSpawner;
import com.mcquest.server.constants.Instances;
import com.mcquest.server.load.InstanceLoader;
import com.mcquest.server.load.ItemLoader;
import com.mcquest.server.load.PlayerClassLoader;
import com.mcquest.server.load.QuestLoader;
import com.mcquest.server.npc.Wolf;
import com.mcquest.server.util.TextUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minestom.server.coordinate.Pos;

public class Main {
    private static final String SERVER_ADDRESS = "0.0.0.0";
    private static final int SERVER_PORT = 25565;

    public static void main(String[] args) {
        TextComponent[] lines = TextUtility.wordWrap("I'm a little teapot, short and stout", 3);
        for (TextComponent line : lines) {
            System.out.println(line.content());
        }
//        Mmorpg.init();
//        load();
//        NonPlayerCharacterSpawner.add(new Wolf(Instances.ELADRADOR, new Pos(0, 70, 0)));
//        Lobby.createLobby();
//        Mmorpg.start(SERVER_ADDRESS, SERVER_PORT);
    }

    private static void load() {
        InstanceLoader.loadInstances();
        PlayerClassLoader.loadPlayerClasses();
        ItemLoader.loadItems();
        QuestLoader.loadQuests();
    }
}
