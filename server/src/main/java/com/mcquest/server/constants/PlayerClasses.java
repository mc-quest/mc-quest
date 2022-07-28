package com.mcquest.server.constants;

import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.playerclass.PlayerClassManager;

/**
 * Provides references to PlayerClasses loaded by the PlayerClassLoader.
 */
public class PlayerClasses {
    public static final PlayerClass FIGHTER = PlayerClassManager.getPlayerClass("Fighter");
    public static final PlayerClass MAGE = PlayerClassManager.getPlayerClass("Mage");
}
