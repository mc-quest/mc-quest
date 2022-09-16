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
    private final PlayerClass[] playerClasses;
    private final int level;
    private final ArmorSlot slot;
    private final double protections;

    ArmorItem(ArmorItemBuilder builder) {
        super(builder);
        this.playerClasses = builder.playerClasses.toArray(new PlayerClass[0]);
        this.slot = builder.slot;
        this.level = builder.level;
        this.protections = builder.protections;
    }

    public int getPlayerClassCount() {
        return playerClasses.length;
    }

    public PlayerClass getPlayerClass(int index) {
        return playerClasses[index];
    }

    /**
     * Returns the slot this ArmorItem occupies.
     */
    public ArmorSlot getSlot() {
        return slot;
    }

    /**
     * Returns the minimum level required to equip this ArmorItem.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns how many protections this ArmorItem provides.
     */
    public double getProtections() {
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
        // lore.append(playerClass);
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
