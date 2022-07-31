package com.mcquest.server.item;

import com.mcquest.server.playerclass.PlayerClassManager;
import com.mcquest.server.playerclass.PlayerClass;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

/**
 * An ArmorItem is an Item that can be equipped by a PlayerCharacter to provide
 * protection.
 */
public class ArmorItem extends Item {
    private final String playerClass;
    private final Slot slot;
    private final int level;
    private final double protections;

    /**
     * Constructs an ArmorItem with the given name, rarity, icon, description,
     * player class, slot, level, and protections.
     */
    public ArmorItem(String name, ItemRarity rarity, Material icon,
                     String description, String playerClass, Slot slot,
                     int level, double protections) {
        super(name, rarity, icon, description);
        this.playerClass = playerClass;
        this.slot = slot;
        this.level = level;
        this.protections = protections;
    }

    /**
     * Constructs an ArmorItem with the given name, rarity, icon, description,
     * PlayerClass, slot, level, and protections.
     */
    public ArmorItem(String name, ItemRarity rarity, Material icon,
                     String description, PlayerClass playerClass,
                     Slot slot, int level, double protections) {
        this(name, rarity, icon, description, playerClass.getName(), slot,
                level, protections);
    }

    /**
     * Constructs an ArmorItem with the given name, rarity, icon, player class,
     * slot, level, and protections. The ArmorItem will have no description.
     */
    public ArmorItem(String name, ItemRarity rarity, Material icon,
                     String playerClass, Slot slot, int level,
                     double protections) {
        this(name, rarity, icon, null, playerClass, slot, level, protections);
    }

    /**
     * Constructs an ArmorItem with the given name, rarity, icon, PlayerClass,
     * slot, level, and protections. The ArmorItem will have no description.
     */
    public ArmorItem(String name, ItemRarity rarity, Material icon,
                     PlayerClass playerClass, Slot slot, int level,
                     double protections) {
        this(name, rarity, icon, null, playerClass, slot, level, protections);
    }

    /**
     * Returns the PlayerClass that can equip this ArmorItem.
     */
    public final PlayerClass getPlayerClass() {
        return PlayerClassManager.getPlayerClass(playerClass);
    }

    /**
     * Returns the slot this ArmorItem occupies.
     */
    public final Slot getSlot() {
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
    protected @NotNull ItemStack createItemStack() {
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

    /**
     * A Slot describes the equipment slot that an ArmorItem occupies.
     */
    public enum Slot {
        FEET("Feet"), LEGS("Legs"), CHEST("Chest"), HEAD("Head");

        private final String text;

        Slot(String text) {
            this.text = text;
        }

        /**
         * Returns the text that describes this Slot.
         */
        public String getText() {
            return text;
        }
    }
}
