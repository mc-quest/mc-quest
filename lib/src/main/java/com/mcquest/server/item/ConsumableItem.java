package com.mcquest.server.item;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

/**
 * A ConsumableItem is an Item that can be consumed by a PlayerCharacter to
 * have some effect.
 */
public class ConsumableItem extends Item {
    private final int level;

    /**
     * Constructs a ConsumableItem with the given name, rarity, icon,
     * description, and level.
     */
    public ConsumableItem(String name, ItemRarity rarity, Material icon,
                          String description, int level) {
        super(name, rarity, icon, description);
        this.level = level;
    }

    /**
     * Constructs a ConsumableItem with the given name, rarity, icon,
     * description, and level. The ConsumableItem will have no description.
     */
    public ConsumableItem(String name, ItemRarity rarity, Material icon,
                          int level) {
        this(name, rarity, icon, null, level);
    }

    /**
     * Returns the minimum level required to use this ConsumableItem.
     */
    public int getLevel() {
        return level;
    }

    @Override
    protected ItemStack createItemStack() {
        String name = getName();
        ItemRarity rarity = getRarity();
        Material icon = getIcon();
        String description = getDescription();
        StringBuilder lore = new StringBuilder();
        // lore.append(ChatColor.GRAY);
        lore.append(rarity.getText());
        lore.append(" Item\n");
        // lore.append(ChatColor.GOLD);
        lore.append("Consumable\nLevel ");
        lore.append(level);
        if (description != null) {
            lore.append("\n\n");
            // lore.append(ChatColor.WHITE);
            lore.append(description);
        }
        lore.append("\n\n");
        // lore.append(ChatColor.GRAY);
        lore.append("Shift-click to use");

        return super.createItemStack();
    }
}
