package com.mcquest.server.load;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.character.PlayerCharacterManager;
import com.mcquest.server.constants.Instances;
import com.mcquest.server.constants.Items;
import com.mcquest.server.constants.PlayerClasses;
import com.mcquest.server.constants.Positions;
import com.mcquest.server.instance.InstanceManager;
import com.mcquest.server.item.ItemManager;
import com.mcquest.server.item.Weapon;
import com.mcquest.server.persistence.PlayerCharacterData;
import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.playerclass.PlayerClassManager;
import com.mcquest.server.ui.PlayerCharacterLogoutType;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;

public class PlayerCharacterSessionHandler {
    public static void handle(Mmorpg mmorpg) {
        PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
        pcManager.setDataProvider(player -> getPlayerCharacterData(mmorpg, player));
        pcManager.setLogoutHandler((pc, logoutType) ->
                handlePlayerCharacterLogout(mmorpg, pc, logoutType));
    }

    private static PlayerCharacterData getPlayerCharacterData(Mmorpg mmorpg, Player player) {
        int playerClassId = PlayerClasses.FIGHTER;
        int instanceId = Instances.ELADRADOR;
        Pos spawnPosition = Positions.SPAWN_POSITION;
        int weaponId = Items.ADVENTURERS_SWORD;

        PlayerClassManager playerClassManager = mmorpg.getPlayerClassManager();
        PlayerClass playerClass = playerClassManager.getPlayerClass(playerClassId);
        InstanceManager instanceManager = mmorpg.getInstanceManager();
        Instance instance = instanceManager.getInstance(instanceId);
        ItemManager itemManager = mmorpg.getItemManager();
        Weapon weapon = (Weapon) itemManager.getItem(weaponId);

        return PlayerCharacterData.create(mmorpg, playerClass, instance, spawnPosition, weapon);
    }

    private static void handlePlayerCharacterLogout(
            Mmorpg mmorpg, PlayerCharacter pc, PlayerCharacterLogoutType logoutType) {
        // TODO
    }
}
