package com.mcquest.core.zone;

import net.kyori.adventure.text.format.NamedTextColor;

import java.awt.*;

public enum ZoneType {
    SETTLEMENT(NamedTextColor.GREEN, Color.GREEN),
    WILDERNESS(NamedTextColor.YELLOW, Color.YELLOW),
    DUNGEON(NamedTextColor.GRAY, Color.GRAY);

    private final NamedTextColor textColor;
    private final Color mapColor;

    ZoneType(NamedTextColor textColor, Color mapColor) {
        this.textColor = textColor;
        this.mapColor = mapColor;
    }

    public NamedTextColor getTextColor() {
        return textColor;
    }

    public Color getMapColor() {
        return mapColor;
    }
}
