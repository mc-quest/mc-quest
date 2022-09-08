package com.mcquest.server.load;

import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.playerclass.PlayerClassManager;
import com.mcquest.server.util.ResourceLoader;

import java.util.List;

public class PlayerClassLoader {
    public static void loadPlayerClasses() {
        List<String> playerClassPaths = ResourceLoader.getResources("playerclasses");
        for (String playerClassPath : playerClassPaths) {
            PlayerClass playerClass = ResourceLoader.deserializeJsonResource(
                    playerClassPath, PlayerClass.class);
        }
    }
}
