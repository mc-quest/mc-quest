package com.mcquest.server.zone;

import net.kyori.adventure.text.format.NamedTextColor;

public enum ZoneType {
    SETTLEMENT(NamedTextColor.GREEN),
    WILDERNESS(NamedTextColor.YELLOW),
    DUNGEON(NamedTextColor.GRAY);

    private final NamedTextColor color;

    ZoneType(NamedTextColor color) {
        this.color = color;
    }

    public NamedTextColor getColor() {
        return color;
    }
}
