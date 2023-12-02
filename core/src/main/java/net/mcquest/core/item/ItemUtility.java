package net.mcquest.core.item;

import net.mcquest.core.resourcepack.Namespaces;
import net.mcquest.core.text.WordWrap;
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

    static TextComponent consumeText() {
        return Component.text("Shift-click to use",
                NamedTextColor.GRAY, TextDecoration.ITALIC);
    }

    static TextComponent useItemText(Item item) {
        return Component.text("Used ", NamedTextColor.GRAY).append(item.getDisplayName());
    }

    static Key resourcePackKey(Item item) {
        return Key.key(Namespaces.ITEMS, String.valueOf(item.getId()));
    }

    static Key resourcePackKey(ConsumableItem item, int cooldownTexture) {
        return Key.key(Namespaces.ITEMS, String.format(item.getId() + "-%d", cooldownTexture));
    }
}
