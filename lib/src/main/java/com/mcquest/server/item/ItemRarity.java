package com.mcquest.server.item;

import net.kyori.adventure.text.format.NamedTextColor;

/**
 * An ItemRarity represents the rarity of an Item.
 */
public enum ItemRarity {
    COMMON("Common", NamedTextColor.GRAY),
    UNCOMMON("Uncommon", NamedTextColor.GREEN),
    RARE("Rare", NamedTextColor.BLUE),
    EPIC("Epic", NamedTextColor.LIGHT_PURPLE),
    LEGENDARY("Legendary", NamedTextColor.GOLD);

    private final String text;
    private final NamedTextColor color;

    ItemRarity(String text, NamedTextColor color) {
        this.text = text;
        this.color = color;
    }

    /**
     * Returns the color that corresponds to this ItemRarity.
     */
    public NamedTextColor getColor() {
        return color;
    }

    /**
     * Returns the text that describes this ItemRarity.
     */
    public String getText() {
        return text;
    }
}
