package com.mcquest.server.item;

import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.util.TextUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;

class ItemUtility {
    static TextComponent rarityText(ItemRarity rarity, String itemType) {
        return Component.text(rarity.getText() + " " + itemType, NamedTextColor.GRAY);
    }

    static List<TextComponent> descriptionText(String description) {
        return TextUtility.wordWrap(description);
    }

    static TextComponent levelText(int level) {
        return Component.text("Level " + level, NamedTextColor.GRAY);
    }

    static TextComponent playerClassText(PlayerClass[] playerClasses) {
        String[] names = new String[playerClasses.length];
        for (int i = 0; i < playerClasses.length; i++) {
            names[i] = playerClasses[i].getName();
        }
        return Component.text(String.join(", ", names), NamedTextColor.GRAY);
    }

    static TextComponent statText(String statName, double stat) {
        return Component.text(((int) Math.round(stat)) + " " + statName, NamedTextColor.GRAY);
    }

    static TextComponent equipText() {
        return Component.text("Shift-click to equip",
                NamedTextColor.GRAY, TextDecoration.ITALIC);
    }

    static TextComponent consumeText() {
        return Component.text("Shift-click to consume",
                NamedTextColor.GRAY, TextDecoration.ITALIC);
    }
}
