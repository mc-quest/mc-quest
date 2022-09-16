package com.mcquest.server;

import com.mcquest.server.character.NonPlayerCharacterSpawner;
import com.mcquest.server.character.PlayerCharacterManager;
import com.mcquest.server.feature.FeatureManager;
import com.mcquest.server.features.TestFeature;
import com.mcquest.server.instance.InstanceManager;
import com.mcquest.server.item.ItemManager;
import com.mcquest.server.item.ItemRarity;
import com.mcquest.server.item.Weapon;
import com.mcquest.server.npc.Dwarf;
import com.mcquest.server.persistence.PlayerCharacterData;
import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.playerclass.PlayerClassManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.Material;

public class Main {
    private static final String SERVER_ADDRESS = "0.0.0.0";
    private static final int SERVER_PORT = 25565;

    public static void main(String[] args) {
        Mmorpg mmorpg = new Mmorpg();

        PlayerClassManager playerClassManager = mmorpg.getPlayerClassManager();
        PlayerClass fighter = playerClassManager.playerClassBuilder("Fighter").build();

        ItemManager itemManager = mmorpg.getItemManager();
        Weapon weapon = itemManager.weaponBuilder(1, "Weapon", ItemRarity.COMMON,
                Material.IRON_SWORD, 1, 5).playerClass(fighter).build();

        InstanceManager instanceManager = mmorpg.getInstanceManager();
        InstanceContainer eladrador = instanceManager.createInstanceContainer("Eladrador");
        eladrador.setChunkLoader(new AnvilLoader("world/eladrador"));

        Pos position = new Pos(0, 70, 0);
        PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
        pcManager.setDataProvider(player -> PlayerCharacterData.create(mmorpg, fighter, eladrador, position, weapon));

        NonPlayerCharacterSpawner npcSpawner = mmorpg.getNonPlayerCharacterSpawner();
        npcSpawner.add(new Dwarf(mmorpg, eladrador, position));

        FeatureManager featureManager = mmorpg.getFeatureManager();
        featureManager.addFeature(new TestFeature());

        mmorpg.start(SERVER_ADDRESS, SERVER_PORT);
    }
}
