package com.mcquest.server.item;

import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

/**
 * A ConsumableItem is an Item that can be consumed by a PlayerCharacter to
 * have some effect.
 */
public class ConsumableItem extends Item {
    private final int level;

    ConsumableItem(int id, @NotNull String name, @NotNull ItemRarity rarity,
                   @NotNull Material icon, @NotNull String description, int level) {
        super(id, name, rarity, icon, description);
        this.level = level;
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
