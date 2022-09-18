package com.mcquest.server.item;

import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A ConsumableItem is an Item that can be consumed by a PlayerCharacter to
 * have some effect.
 */
public class ConsumableItem extends Item {
    private final int level;

    ConsumableItem(ConsumableItemBuilder builder) {
        super(builder);
        this.level = builder.level;
    }

    /**
     * Returns the minimum level required to use this ConsumableItem.
     */
    public int getLevel() {
        return level;
    }

    @Override
    List<Component> getItemStackLore() {
        ItemRarity rarity = getRarity();
        String description = getDescription();
        List<Component> lore = new ArrayList<>();
        lore.add(ItemUtility.rarityText(rarity, "Consumable"));
        lore.add(ItemUtility.levelText(level));
        if (description != null) {
            lore.add(Component.empty());
            lore.addAll(ItemUtility.descriptionText(description));
        }
        lore.add(Component.empty());
        lore.add(ItemUtility.consumeText());
        return lore;
    }
}
