package com.mcquest.server.playerclass;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.ApiStatus;

public class Skill {
    private final String name;
    private final int level;
    private final String description;

    Skill(String name, int level, String description) {
        this.name = name;
        this.level = level;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    @ApiStatus.Internal
    public ItemStack getHotbarItemStack() {
        // TODO
        return ItemStack.of(Material.DIAMOND);
    }
}
