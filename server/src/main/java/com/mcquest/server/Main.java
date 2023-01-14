package com.mcquest.server;

import com.mcquest.server.constants.*;
import com.mcquest.server.db.Database;
import com.mcquest.server.util.ResourceUtility;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.file.FileTree;

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
                .instances(Instances.all())
                .models(Models.all())
                .music(Music.all())
                .maps(Maps.all())
                .features(Features.all())
                .resourcePack(Main::writeResourcePack)
                .playerCharacterDataProvider(database::getPlayerCharacterData)
                .playerCharacterLogoutHandler(database::savePlayerCharacterData)
                .start(SERVER_ADDRESS, SERVER_PORT, RESOURCE_PACK_SERVER_PORT);
    }

    private static void extractWorldResources() {
        ResourceUtility.extractResources("worlds", "world");
    }

    private static void writeResourcePack(FileTree tree) {
        String basePath = "resourcepack";
        List<String> paths = ResourceUtility.getResources(basePath);
        for (String path : paths) {
            String subPath = path.substring(basePath.length() + 1);
            tree.write(subPath, Writable.inputStream(() ->
                    ResourceUtility.getStream(path)));
        }
    }
}
