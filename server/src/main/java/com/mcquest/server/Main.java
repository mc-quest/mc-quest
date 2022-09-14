package com.mcquest.server;

import com.mcquest.server.character.PlayerCharacterManager;
import com.mcquest.server.feature.FeatureManager;
import com.mcquest.server.features.TestFeature;
import com.mcquest.server.instance.InstanceManager;
import com.mcquest.server.item.ItemManager;
import com.mcquest.server.item.ItemRarity;
import com.mcquest.server.item.Weapon;
import com.mcquest.server.music.MusicManager;
import com.mcquest.server.music.Song;
import com.mcquest.server.persistence.PlayerCharacterData;
import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.playerclass.PlayerClassManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;

public class Main {
    private static final String SERVER_ADDRESS = "0.0.0.0";
    private static final int SERVER_PORT = 25565;

    public static void main(String[] args) {
        Mmorpg mmorpg = new Mmorpg();

        PlayerClassManager playerClassManager = mmorpg.getPlayerClassManager();
        PlayerClass fighter = playerClassManager.playerClassBuilder("Fighter").build();

        ItemManager itemManager = mmorpg.getItemManager();
        Weapon weapon = itemManager.createWeapon(1, "Weapon", ItemRarity.COMMON,
                Material.IRON_SWORD, "Description here §1test test test.",
                fighter, 1, 5);

        InstanceManager instanceManager = mmorpg.getInstanceManager();
        InstanceContainer eladrador = instanceManager.createInstanceContainer("Eladrador");
        eladrador.setChunkLoader(new AnvilLoader("world/eladrador"));

        Pos position = new Pos(0, 70, 0);
        PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
        pcManager.setDataProvider(player -> PlayerCharacterData.create(mmorpg, fighter, eladrador, position, weapon));

        MusicManager musicManager = mmorpg.getMusicManager();
        Song song = musicManager.songBuilder(1, Duration.ofSeconds(5))
                .tone(Duration.ofMillis(0), SoundEvent.BLOCK_NOTE_BLOCK_HARP, 1f, 1f)
                .tone(Duration.ofMillis(250), SoundEvent.BLOCK_NOTE_BLOCK_HARP, 1f, 1f)
                .tone(Duration.ofMillis(500), SoundEvent.BLOCK_NOTE_BLOCK_HARP, 1f, 1f)
                .tone(Duration.ofMillis(750), SoundEvent.BLOCK_NOTE_BLOCK_HARP, 1f, 1f)
                .tone(Duration.ofMillis(1000), SoundEvent.BLOCK_NOTE_BLOCK_HARP, 1f, 1f)
                .tone(Duration.ofMillis(1250), SoundEvent.BLOCK_NOTE_BLOCK_HARP, 1f, 1f)
                .build();

        FeatureManager featureManager = mmorpg.getFeatureManager();
        featureManager.addFeature(new TestFeature());

        mmorpg.start(SERVER_ADDRESS, SERVER_PORT);
    }
}
