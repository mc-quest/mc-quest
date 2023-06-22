package com.mcquest.core.item;

import com.mcquest.core.resourcepack.Namespaces;
import com.mcquest.core.text.WordWrap;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.List;

class ItemUtility {
    static TextComponent qualityText(ItemQuality quality, String itemType) {
        return Component.text(quality.getText() + " " + itemType, NamedTextColor.GRAY);
    }

    static List<TextComponent> descriptionText(String description) {
        return WordWrap.wrap(description);
    }

    static TextComponent levelText(int level) {
        return Component.text("Level " + level, NamedTextColor.GRAY);
    }

    static TextComponent statText(String statName, double stat) {
        return Component.text(((int) Math.round(stat)) + " " + statName, NamedTextColor.YELLOW);
    }

    static TextComponent equipText() {
        return Component.text("Shift-click to equip",
                NamedTextColor.GRAY, TextDecoration.ITALIC);
    }

    static TextComponent consumeText() {
        return Component.text("Shift-click to use",
                NamedTextColor.GRAY, TextDecoration.ITALIC);
    }

    static Key resourcePackKey(Item item) {
        return Key.key(Namespaces.ITEMS, String.valueOf(item.getId()));
    }

    static Key resourcePackKey(ConsumableItem item, int cooldownTexture) {
        return Key.key(Namespaces.ITEMS,
                String.format("%d-%d", item.getId(), cooldownTexture));
    }
}
