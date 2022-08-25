package com.mcquest.server.playerclass;

import com.mcquest.server.util.HashableItemStack;
import net.minestom.server.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * The PlayerClassManager is used to register and retrieve PlayerClasses.
 */
public class PlayerClassManager {
    private static final Map<String, PlayerClass> playerClassesByName = new HashMap<>();
    private static final Map<HashableItemStack, Skill> skillsByItemStack = new HashMap<>();

    /**
     * Registers a PlayerClass with the MMORPG.
     */
    public static void registerPlayerClass(PlayerClass playerClass) {
        String name = playerClass.getName();
        if (playerClassesByName.containsKey(name)) {
            throw new IllegalArgumentException(
                    "Attempted to register a player class with a name that is already registered: " + name);
        }
        for (int i = 0; i < playerClass.getSkillCount(); i++) {
            Skill skill = playerClass.getSkill(i);
            ItemStack hotbarItemStack = skill.getHotbarItemStack();
        }
        playerClassesByName.put(playerClass.getName(), playerClass);
    }

    /**
     * Returns the PlayerClass with the given name.
     */
    public static PlayerClass getPlayerClass(String name) {
        return playerClassesByName.get(name);
    }

    public static Skill getSkill(ItemStack hotbarItemStack) {
        return skillsByItemStack.get(hotbarItemStack);
    }
}
