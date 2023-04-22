package com.mcquest.server;

import com.mcquest.server.asset.Asset;
import com.mcquest.server.asset.AssetDirectory;
import com.mcquest.server.constants.*;
import com.mcquest.server.db.Database;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.file.FileTree;

import java.io.File;
import java.util.List;

public class Main {
    private static final String SERVER_ADDRESS = "0.0.0.0";
    private static final int SERVER_PORT = 25565;
    private static final int RESOURCE_PACK_SERVER_PORT = 7270;

    public static void main(String[] args) {
        extractWorldResources();
        Database database = new Database();
        Mmorpg.builder()
                .playerClasses(PlayerClasses.all())
                .items(Items.all())
                .quests(Quests.all())
                .zones(Zones.all())
                .music(Music.all())
                .maps(Maps.all())
                .mounts(Mounts.all())
                .instances(Instances.all())
                .models(Models.all())
                .audio(AudioClips.all())
                .features(Features.all())
                .resourcePack(Main::writeResourcePack)
                .playerCharacterDataProvider(database::getPlayerCharacterData)
                .playerCharacterLogoutHandler(database::savePlayerCharacterData)
                .start(SERVER_ADDRESS, SERVER_PORT, RESOURCE_PACK_SERVER_PORT);
    }

    private static void extractWorldResources() {
        AssetDirectory worldsDir = Assets.directory("worlds");
        File worldDir = new File("world");
        worldsDir.extractAssets(worldDir);
    }

    private static void writeResourcePack(FileTree tree) {
        String basePath = "resourcepack";
        AssetDirectory resourcePackDir = Assets.directory(basePath);
        List<Asset> assets = resourcePackDir.getAssets();
        for (Asset asset : assets) {
            String subPath = asset.getPath().substring(basePath.length() + 1);
            tree.write(subPath, Writable.inputStream(asset::getStream));
        }
    }
}
