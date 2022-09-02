package com.mcquest.server;

import com.mcquest.server.character.NonPlayerCharacterSpawner;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.character.PlayerCharacterManager;
import com.mcquest.server.constants.Instances;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.item.Item;
import com.mcquest.server.load.InstanceLoader;
import com.mcquest.server.load.ItemLoader;
import com.mcquest.server.load.PlayerClassLoader;
import com.mcquest.server.load.QuestLoader;
import com.mcquest.server.npc.Wolf;
import com.mcquest.server.persistence.PlayerCharacterData;
import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.quest.Quest;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;

import java.util.function.Consumer;
import java.util.function.Function;

public class Main {
    private static final String SERVER_ADDRESS = "0.0.0.0";
    private static final int SERVER_PORT = 25565;

    public static void main(String[] args) {
        Item[] items = {};
        Quest[] quests = {};
        PlayerClass[] playerClasses = {};
        Feature[] features = {};
        Function<Player, PlayerCharacterData> pcDataRetriever = player -> {
            return PlayerCharacterData.create();
        };
        Consumer<PlayerCharacter> pcQuitHandler = pc -> {
        };
        Mmorpg mmorpg = new Mmorpg(items, quests, playerClasses, features, pcDataRetriever, pcQuitHandler);
        mmorpg.start(SERVER_ADDRESS, SERVER_PORT);
    }
}
