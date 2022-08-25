package com.mcquest.server.playerclass;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.ApiStatus;

public class Skill {
    private final String name;
    private final int level;
    private final String description;
    private final int skillTreeRow;
    private final int skillTreeColumn;

    public Skill(String name, int level, String description, int skillTreeRow, int skillTreeColumn) {
        this.name = name;
        this.level = level;
        this.description = description;
        this.skillTreeRow = skillTreeRow;
        this.skillTreeColumn = skillTreeColumn;
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

    public int getSkillTreeRow() {
        return skillTreeRow;
    }

    public int getSkillTreeColumn() {
        return skillTreeColumn;
    }

    @ApiStatus.Internal
    public ItemStack getHotbarItemStack() {
        // TODO
        return ItemStack.of(Material.DIAMOND);
    }
}
