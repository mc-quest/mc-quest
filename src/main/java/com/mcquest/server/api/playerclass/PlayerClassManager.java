package com.mcquest.server.api.playerclass;

import java.util.HashMap;
import java.util.Map;

/**
 * The PlayerClassManager is used to register and retrieve PlayerClasses.
 */
public class PlayerClassManager {
    private static final Map<String, PlayerClass> playerClassesByName = new HashMap<>();

    /**
     * Registers a PlayerClass with the MMORPG.
     */
    public static void registerPlayerClass(PlayerClass playerClass) {
        String name = playerClass.getName();
        if (playerClassesByName.containsKey(name)) {
            throw new IllegalArgumentException(
                    "Attempted to register a player class with a name that is already registered: " + name);
        }
        playerClassesByName.put(playerClass.getName(), playerClass);
    }

    /**
     * Returns the PlayerClass with the given name.
     */
    public static PlayerClass getPlayerClass(String name) {
        return playerClassesByName.get(name);
    }
}
