package com.mcquest.server;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.constants.*;
import com.mcquest.server.persistence.PlayerCharacterData;
import com.mcquest.server.ui.PlayerCharacterLogoutType;
import com.mcquest.server.util.ResourceUtility;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;

public class Main {
    private static final String SERVER_ADDRESS = "0.0.0.0";
    private static final int SERVER_PORT = 25565;
    private static final int RESOURCE_PACK_SERVER_PORT = 7270;

    public static void main(String[] args) {
        ResourceUtility.extractResources("worlds", "world");
        Mmorpg.builder()
                .playerClasses(PlayerClasses.FIGHTER, PlayerClasses.MAGE)
                .items(Items.ADVENTURERS_SWORD)
                .quests(Quests.TUTORIAL)
                .instances(Instances.ELADRADOR)
                .models(Models.WOLF_SPIDER)
                .music(Music.DUNGEON)
                .features(Features.FIGHTER_PLAYER_CLASS, Features.MAGE_PLAYER_CLASS, Features.TUTORIAL_QUEST)
                .playerCharacterDataProvider(Main::playerCharacterDataProvider)
                .playerCharacterLogoutHandler(Main::playerCharacterLogoutHandler)
                .start(SERVER_ADDRESS, SERVER_PORT, RESOURCE_PACK_SERVER_PORT);
    }

    private static PlayerCharacterData playerCharacterDataProvider(Player player) {
        return PlayerCharacterData.create(PlayerClasses.FIGHTER,
                Instances.ELADRADOR, new Pos(0, 70, 0), Items.ADVENTURERS_SWORD);
    }

    private static void playerCharacterLogoutHandler(PlayerCharacter pc,
                                                     PlayerCharacterLogoutType logoutType) {
        if (logoutType == PlayerCharacterLogoutType.MENU_LOGOUT) {
            pc.getPlayer().kick("You logged out");
        }
        System.out.println(pc.getPlayer().getUsername() + " logged out");
    }
}
