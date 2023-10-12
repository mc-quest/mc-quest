package net.mcquest.core.item;

import net.kyori.adventure.text.format.NamedTextColor;

public enum ItemQuality {
    POOR("Poor", NamedTextColor.GRAY),
    COMMON("Common", NamedTextColor.WHITE),
    UNCOMMON("Uncommon", NamedTextColor.GREEN),
    RARE("Rare", NamedTextColor.BLUE),
    EPIC("Epic", NamedTextColor.DARK_PURPLE),
    LEGENDARY("Legendary", NamedTextColor.GOLD);

    private final String text;
    private final NamedTextColor color;

    ItemQuality(String text, NamedTextColor color) {
        this.text = text;
        this.color = color;
    }

    public NamedTextColor getColor() {
        return color;
    }

    public String getText() {
        return text;
    }
}
