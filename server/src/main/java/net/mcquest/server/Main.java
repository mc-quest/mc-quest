package net.mcquest.server;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.asset.AssetDirectory;
import net.mcquest.server.constants.*;
import net.mcquest.server.persistence.InMemoryPersistenceService;

import java.io.File;

public class Main {
    public static final String MMORPG_NAME = "MCQuest";
    private static final String SERVER_ADDRESS = "0.0.0.0";
    private static final int SERVER_PORT = 25565;
    private static final int RESOURCE_PACK_SERVER_PORT = 7270;

    public static void main(String[] args) {
        extractWorldResources();
        Mmorpg.builder()
                .name(MMORPG_NAME)
                .playerClasses(PlayerClasses.all())
                .items(Items.all())
                .quests(Quests.all())
                .zones(Zones.all())
                .music(Music.all())
                .maps(Maps.all())
                .mounts(Mounts.all())
                .instances(Instances.all())
                .biomes(Biomes.all())
                .models(Models.all())
                .audio(AudioClips.all())
                .features(Features.all())
                .persistenceService(new InMemoryPersistenceService())
                .start(SERVER_ADDRESS, SERVER_PORT, RESOURCE_PACK_SERVER_PORT);
    }

    private static void extractWorldResources() {
        AssetDirectory worldsDir = Assets.directory("worlds");
        File worldDir = new File("world");
        worldsDir.extractAssets(worldDir);
    }
}
