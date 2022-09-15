package com.mcquest.server;

import com.mcquest.server.character.PlayerCharacterManager;
import com.mcquest.server.feature.FeatureManager;
import com.mcquest.server.features.TestFeature;
import com.mcquest.server.instance.InstanceManager;
import com.mcquest.server.item.ItemManager;
import com.mcquest.server.item.ItemRarity;
import com.mcquest.server.item.Weapon;
import com.mcquest.server.music.MusicManager;
import com.mcquest.server.music.Pitch;
import com.mcquest.server.music.Song;
import com.mcquest.server.persistence.PlayerCharacterData;
import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.playerclass.PlayerClassManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.item.Material;
import net.minestom.server.sound.SoundEvent;

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
        Song song = musicManager.songBuilder(1, 24, 120)
                .instrument(SoundEvent.BLOCK_NOTE_BLOCK_HARP)

                .note(1, Pitch.C_1)
                .note(2, Pitch.D_SHARP_1)
                .note(3, Pitch.G_2)
                .note(4, Pitch.C_1)
                .note(5, Pitch.D_SHARP_1)
                .note(6, Pitch.G_2)
                .note(7, Pitch.C_1)
                .note(8, Pitch.D_SHARP_1)
                .note(9, Pitch.G_2)
                .note(10, Pitch.C_1)
                .note(11, Pitch.D_SHARP_1)
                .note(12, Pitch.G_2)

                .note(13, Pitch.B_1)
                .note(14, Pitch.D_1)
                .note(15, Pitch.F_1)
                .note(16, Pitch.B_1)
                .note(17, Pitch.D_1)
                .note(18, Pitch.F_1)
                .note(19, Pitch.B_1)
                .note(20, Pitch.D_1)
                .note(21, Pitch.F_1)
                .note(22, Pitch.B_1)
                .note(23, Pitch.D_1)
                .note(24, Pitch.F_1)

                .instrument(SoundEvent.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE)

                .note(1, Pitch.C_1)
                .note(2, Pitch.C_1)
                .note(3, Pitch.C_1)
                .note(4, Pitch.C_1)
                .note(5, Pitch.C_1)
                .note(6, Pitch.C_1)
                .note(7, Pitch.C_1)
                .note(8, Pitch.C_1)
                .note(9, Pitch.C_1)
                .note(10, Pitch.C_1)
                .note(11, Pitch.C_1)
                .note(12, Pitch.C_1)

                .note(13, Pitch.B_1)
                .note(14, Pitch.B_1)
                .note(15, Pitch.B_1)
                .note(16, Pitch.B_1)
                .note(17, Pitch.B_1)
                .note(18, Pitch.B_1)
                .note(19, Pitch.B_1)
                .note(20, Pitch.B_1)
                .note(21, Pitch.B_1)
                .note(22, Pitch.B_1)
                .note(23, Pitch.B_1)
                .note(24, Pitch.B_1)

                .build();

        FeatureManager featureManager = mmorpg.getFeatureManager();
        featureManager.addFeature(new TestFeature());

        mmorpg.start(SERVER_ADDRESS, SERVER_PORT);
    }
}
