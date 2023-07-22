package com.mcquest.core.character;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public enum Attitude {
    FRIENDLY(NamedTextColor.GREEN),
    NEUTRAL(NamedTextColor.YELLOW),
    HOSTILE(NamedTextColor.RED);

    private final TextColor color;

    Attitude(TextColor color) {
        this.color = color;
    }

    public TextColor getColor() {
        return color;
    }
}
