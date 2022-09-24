package com.mcquest.server;

import com.mcquest.server.load.*;

public class Main {
    private static final String SERVER_ADDRESS = "0.0.0.0";
    private static final int SERVER_PORT = 25565;
    private static final int RESOURCE_PACK_SERVER_PORT = 7270;

    public static void main(String[] args) {
        Mmorpg mmorpg = new Mmorpg();
        PlayerClassLoader.loadPlayerClasses(mmorpg);
        ItemLoader.loadItems(mmorpg);
        QuestLoader.loadQuests(mmorpg);
        MusicLoader.loadMusic(mmorpg);
        InstanceLoader.loadInstances(mmorpg);
        PlayerCharacterSessionHandler.handle(mmorpg);
        FeatureLoader.loadFeatures(mmorpg);
        mmorpg.start(SERVER_ADDRESS, SERVER_PORT, RESOURCE_PACK_SERVER_PORT);
    }
}
