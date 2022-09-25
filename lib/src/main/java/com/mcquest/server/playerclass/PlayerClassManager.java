package com.mcquest.server.playerclass;

import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The PlayerClassManager is used to register and retrieve PlayerClasses.
 */
public class PlayerClassManager {
    static final Tag<Integer> PLAYER_CLASS_ID_TAG = Tag.Integer("player_class_id");
    static final Tag<Integer> SKILL_ID_TAG = Tag.Integer("skill_id");

    private final Map<Integer, PlayerClass> playerClassesById;

    @ApiStatus.Internal
    public PlayerClassManager() {
        playerClassesById = new HashMap<>();
    }

    public PlayerClassBuilder playerClassBuilder(int id, String name) {
        return new PlayerClassBuilder(this, id, name);
    }

    void registerPlayerClass(PlayerClass playerClass) {
        int id = playerClass.getId();
        if (playerClassesById.containsKey(id)) {
            throw new IllegalArgumentException("ID already in use: " + id);
        }
        for (Skill skill : playerClass.getSkills()) {
            ItemStack hotbarItemStack = skill.getHotbarItemStack();
        }
        playerClassesById.put(playerClass.getId(), playerClass);
    }

    /**
     * Returns the PlayerClass with the given name.
     */
    public PlayerClass getPlayerClass(int id) {
        return playerClassesById.get(id);
    }

    public Collection<PlayerClass> getPlayerClasses() {
        return Collections.unmodifiableCollection(playerClassesById.values());
    }

    public Skill getSkill(ItemStack hotbarItemStack) {
        if (!hotbarItemStack.hasTag(PLAYER_CLASS_ID_TAG) ||
                !hotbarItemStack.hasTag(SKILL_ID_TAG)) {
            return null;
        }
        int playerClassId = hotbarItemStack.getTag(PLAYER_CLASS_ID_TAG);
        PlayerClass playerClass = getPlayerClass(playerClassId);
        if (playerClass == null) {
            return null;
        }
        int skillId = hotbarItemStack.getTag(SKILL_ID_TAG);
        return playerClass.getSkill(skillId);
    }
}
