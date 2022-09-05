package com.mcquest.server;

import com.mcquest.server.character.PlayerCharacterManager;
import com.mcquest.server.instance.InstanceManager;
import com.mcquest.server.item.ItemManager;
import com.mcquest.server.load.InstanceLoader;
import com.mcquest.server.load.ItemLoader;
import com.mcquest.server.load.QuestLoader;
import com.mcquest.server.persistence.PlayerCharacterData;

public class Main {
    private static final String SERVER_ADDRESS = "0.0.0.0";
    private static final int SERVER_PORT = 25565;

    public static void main(String[] args) {
        Mmorpg mmorpg = new Mmorpg();
        PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
        pcManager.setDataProvider(player -> new PlayerCharacterData());
        ItemLoader itemLoader = new ItemLoader();
        itemLoader.loadItems(mmorpg.getItemManager());
        QuestLoader questLoader = new QuestLoader();
        questLoader.loadQuests(mmorpg.getQuestManager());
        InstanceManager instanceManager = mmorpg.getInstanceManager();
        InstanceLoader instanceLoader = new InstanceLoader();
        instanceLoader.loadInstances(instanceManager);
        mmorpg.start(SERVER_ADDRESS, SERVER_PORT);
    }
}
