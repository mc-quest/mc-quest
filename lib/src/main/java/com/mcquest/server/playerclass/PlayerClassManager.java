package com.mcquest.server.playerclass;

import com.mcquest.server.util.HashableItemStack;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * The PlayerClassManager is used to register and retrieve PlayerClasses.
 */
public class PlayerClassManager {
    private final Map<String, PlayerClass> playerClassesByName;
    private final Map<HashableItemStack, Skill> skillsByItemStack;

    @ApiStatus.Internal
    public PlayerClassManager() {
        playerClassesByName = new HashMap<>();
        skillsByItemStack = new HashMap<>();
    }

    public void registerPlayerClass(PlayerClass playerClass) {
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
    public PlayerClass getPlayerClass(String name) {
        return playerClassesByName.get(name);
    }

    public Skill getSkill(ItemStack hotbarItemStack) {
        return skillsByItemStack.get(hotbarItemStack);
    }
}
