package com.mcquest.server;

import com.mcquest.server.character.PlayerCharacterManager;
import com.mcquest.server.feature.FeatureManager;
import com.mcquest.server.features.TestFeature;
import com.mcquest.server.instance.InstanceManager;
import com.mcquest.server.item.*;
import com.mcquest.server.persistence.PlayerCharacterData;
import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.playerclass.PlayerClassManager;
import com.mcquest.server.ui.PlayerCharacterLogoutType;
import com.mcquest.server.util.ResourceLoader;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.Material;

public class Main {
    private static final String SERVER_ADDRESS = "0.0.0.0";
    private static final int SERVER_PORT = 25565;
    private static final int RESOURCE_PACK_SERVER_PORT = 7270;

    public static void main(String[] args) {
        ResourceLoader.extractResources("worlds", "world");
        Mmorpg mmorpg = new Mmorpg();

        PlayerClassManager playerClassManager = mmorpg.getPlayerClassManager();
        PlayerClass fighter = playerClassManager.playerClassBuilder("Fighter")
                .skill("Bash", 1, "Damages enemies in front of you")
                .skillTree(builder -> {

                })
                .build();
        PlayerClass mage = playerClassManager.playerClassBuilder("Mage").build();

        ItemManager itemManager = mmorpg.getItemManager();
        Weapon weapon = itemManager.weaponBuilder(1, "Weapon", ItemRarity.LEGENDARY,
                        Material.IRON_SWORD, 1, 5).addPlayerClass(fighter)
                .addPlayerClass(mage)
                .build();

        InstanceManager instanceManager = mmorpg.getInstanceManager();
        InstanceContainer eladrador = instanceManager.createInstanceContainer("Eladrador");
        eladrador.setChunkLoader(new AnvilLoader("world/eladrador"));

        Pos position = new Pos(0, 70, 0);
        PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
        pcManager.setDataProvider(player -> PlayerCharacterData.create(mmorpg, fighter, eladrador, position, weapon));
        pcManager.setLogoutHandler((pc, logoutType) -> {
            System.out.println("Logging out");
        });

        FeatureManager featureManager = mmorpg.getFeatureManager();
        featureManager.addFeature(new TestFeature());

        mmorpg.start(SERVER_ADDRESS, SERVER_PORT, RESOURCE_PACK_SERVER_PORT);
    }
}
