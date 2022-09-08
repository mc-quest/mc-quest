package com.mcquest.server.item;

import com.mcquest.server.playerclass.PlayerClass;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

/**
 * An ArmorItem is an Item that can be equipped by a PlayerCharacter to provide
 * protection.
 */
public class ArmorItem extends Item {
    private final PlayerClass playerClass;
    private final ArmorSlot slot;
    private final int level;
    private final double protections;

    /**
     * Constructs an ArmorItem with the given name, rarity, icon, description,
     * player class, slot, level, and protections.
     */
    ArmorItem(@NotNull String name, @NotNull ItemRarity rarity, @NotNull Material icon,
              @NotNull String description, @NotNull PlayerClass playerClass,
              int level, @NotNull ArmorSlot slot, double protections) {
        super(name, rarity, icon, description);
        this.playerClass = playerClass;
        this.slot = slot;
        this.level = level;
        this.protections = protections;
    }

    /**
     * Returns the PlayerClass that can equip this ArmorItem.
     */
    public final PlayerClass getPlayerClass() {
        return playerClass;
    }

    /**
     * Returns the slot this ArmorItem occupies.
     */
    public final ArmorSlot getSlot() {
        return slot;
    }

    /**
     * Returns the minimum level required to equip this ArmorItem.
     */
    public final int getLevel() {
        return level;
    }

    /**
     * Returns how many protections this ArmorItem provides.
     */
    public final double getProtections() {
        return protections;
    }

    @Override
    @NotNull ItemStack createItemStack() {
        // TODO
        String name = getName();
        ItemRarity rarity = getRarity();
        Material icon = getIcon();
        String description = getDescription();
        StringBuilder lore = new StringBuilder();
        // lore.append(ChatColor.GRAY);
        lore.append(rarity.getText());
        lore.append(" Item\n");
        // lore.append(ChatColor.GOLD);
        lore.append(playerClass);
        lore.append(" Armor\n");
        lore.append(slot.getText());
        lore.append("\nLevel ");
        lore.append(level);
        lore.append('\n');
        lore.append((int) Math.round(protections));
        lore.append(" Protections");
        if (description != null) {
            lore.append("\n\n");
            // lore.append(ChatColor.WHITE);
            lore.append(description);
        }
        lore.append("\n\n");
        // lore.append(ChatColor.GRAY);
        lore.append("Shift-click to equip");
        // return BukkitUtility.createItemStack(icon, rarity.getColor() + name, lore.toString());
        return super.createItemStack();
    }
}
