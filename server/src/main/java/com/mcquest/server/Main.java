package com.mcquest.server;

import com.mcquest.server.constants.*;
import com.mcquest.server.db.Database;
import com.mcquest.server.util.ResourceUtility;

public class Main {
    private static final String SERVER_ADDRESS = "0.0.0.0";
    private static final int SERVER_PORT = 25565;
    private static final int RESOURCE_PACK_SERVER_PORT = 7270;

    public static void main(String[] args) {
        extractWorldResources();
        Database database = new Database();
        Mmorpg.builder()
                .playerClasses(PlayerClasses.FIGHTER, PlayerClasses.MAGE)
                .items(Items.ADVENTURERS_SWORD)
                .quests(Quests.TUTORIAL)
                .instances(Instances.ELADRADOR)
                .models(Models.WOLF_SPIDER)
                .music(Music.DUNGEON)
                .maps(Maps.MELCHER)
                .features(Features.FIGHTER_PLAYER_CLASS, Features.MAGE_PLAYER_CLASS, Features.TUTORIAL_QUEST)
                .playerCharacterDataProvider(database::getPlayerCharacterData)
                .playerCharacterLogoutHandler(database::savePlayerCharacterData)
                .start(SERVER_ADDRESS, SERVER_PORT, RESOURCE_PACK_SERVER_PORT);
    }

    private static void extractWorldResources() {
        ResourceUtility.extractResources("worlds", "world");
    }
}
