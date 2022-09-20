package com.mcquest.server;

import com.mcquest.server.character.PlayerCharacterManager;
import com.mcquest.server.event.PlayerCharacterLoginEvent;
import com.mcquest.server.feature.FeatureManager;
import com.mcquest.server.features.TestFeature;
import com.mcquest.server.instance.InstanceManager;
import com.mcquest.server.item.*;
import com.mcquest.server.music.MusicManager;
import com.mcquest.server.music.Pitch;
import com.mcquest.server.music.Song;
import com.mcquest.server.persistence.PlayerCharacterData;
import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.playerclass.PlayerClassManager;
import com.mcquest.server.util.ResourceLoader;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

public class Main {
    private static final String SERVER_ADDRESS = "0.0.0.0";
    private static final int SERVER_PORT = 25565;
    private static final int RESOURCE_PACK_SERVER_PORT = 7270;

    public static void main(String[] args) {
        ResourceLoader.extractResources("worlds", "world");
        Mmorpg mmorpg = new Mmorpg();

        PlayerClassManager playerClassManager = mmorpg.getPlayerClassManager();
        PlayerClass fighter = playerClassManager.playerClassBuilder(1, "Fighter")
                .skill(1, "Bash", 1, Material.SHIELD, "Damage enemies in front of you", 0, 4)
                .skillTreeDecoration(Material.DIAMOND, 0, 0)
                .build();
        PlayerClass mage = playerClassManager.playerClassBuilder(2, "Mage").build();

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
