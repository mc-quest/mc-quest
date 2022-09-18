package com.mcquest.server;

import com.mcquest.server.character.NonPlayerCharacterSpawner;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.character.PlayerCharacterManager;
import com.mcquest.server.event.PlayerCharacterLoginEvent;
import com.mcquest.server.feature.FeatureManager;
import com.mcquest.server.features.TestFeature;
import com.mcquest.server.instance.InstanceManager;
import com.mcquest.server.item.*;
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

        PlayerClass mage = playerClassManager.playerClassBuilder("Mage").build();

        ItemManager itemManager = mmorpg.getItemManager();
        Weapon weapon = itemManager.weaponBuilder(1, "Weapon", ItemRarity.LEGENDARY,
                        Material.IRON_SWORD, 1, 5).addPlayerClass(fighter)
                .addPlayerClass(mage)
                .description("This is a very long description for a very boring item.").build();
        ArmorItem armor = itemManager.armorItemBuilder(2, "Armor", ItemRarity.UNCOMMON,
                        Material.CHAINMAIL_HELMET, 23, ArmorSlot.HEAD, 25)
                .description("This is a description for an armor item.")
                .playerClass(fighter)
                .build();
        ConsumableItem potion = itemManager.consumableItemBuilder(3, "Minor Potion of Healing",
                ItemRarity.UNCOMMON, Material.POTION, 1)
                .description("Restores &c20 HP&r.")
                .build();
        Item crops = itemManager.itemBuilder(4, "Crops", ItemRarity.COMMON, Material.WHEAT)
                .description("Stolen by the bandits.")
                .build();

        InstanceManager instanceManager = mmorpg.getInstanceManager();
        InstanceContainer eladrador = instanceManager.createInstanceContainer("Eladrador");
        eladrador.setChunkLoader(new AnvilLoader("world/eladrador"));

        Pos position = new Pos(0, 70, 0);
        PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
        pcManager.setDataProvider(player -> PlayerCharacterData.create(mmorpg, fighter, eladrador, position, weapon));

        FeatureManager featureManager = mmorpg.getFeatureManager();
        featureManager.addFeature(new TestFeature());

        mmorpg.getGlobalEventHandler().addListener(PlayerCharacterLoginEvent.class, event -> {
            PlayerCharacter pc = event.getPlayerCharacter();
            pc.giveItem(armor);
            pc.giveItem(potion);
            pc.giveItem(crops);
        });

        mmorpg.start(SERVER_ADDRESS, SERVER_PORT);
    }
}
