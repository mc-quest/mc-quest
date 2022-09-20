package com.mcquest.server.playerclass;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.ApiStatus;

public class Skill {
    private final int id;
    private final String name;
    private final int level;
    private final Material icon;
    private final String description;
    private final int skillTreeRow;
    private final int skillTreeColumn;
    PlayerClass playerClass;

    Skill(int id, String name, int level, Material icon,
          String description, int skillTreeRow, int skillTreeColumn) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.icon = icon;
        this.description = description;
        this.skillTreeRow = skillTreeRow;
        this.skillTreeColumn = skillTreeColumn;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public Material getIcon() {
        return icon;
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

    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    @ApiStatus.Internal
    public ItemStack getHotbarItemStack() {
        // TODO
        return ItemStack.of(Material.DIAMOND);
    }
}
