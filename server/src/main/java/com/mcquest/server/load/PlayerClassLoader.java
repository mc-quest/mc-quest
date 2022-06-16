package com.mcquest.server.load;

import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.playerclass.PlayerClassManager;
import com.mcquest.server.util.ResourceLoader;

import java.util.List;

public class PlayerClassLoader {
    /**
     * Loads all PlayerClasses and registers them with the PlayerClassManager.
     */
    public static void loadPlayerClasses() {
        List<String> playerClassPaths = ResourceLoader.getResources("playerclasses");
        for (String playerClassPath : playerClassPaths) {
            PlayerClass playerClass = ResourceLoader.deserializeJsonResource(
                    playerClassPath, PlayerClass.class);
            PlayerClassManager.registerPlayerClass(playerClass);
        }
    }
}
