package com.mcquest.server.playerclass;

import net.minestom.server.item.Material;

public class SkillTreeDecoration {
    private final Material icon;
    private final int row;
    private final int column;

    SkillTreeDecoration(Material icon, int row, int column) {
        this.icon = icon;
        this.row = row;
        this.column = column;
    }

    public Material getIcon() {
        return icon;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
