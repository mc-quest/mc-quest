package com.mcquest.server.item;

import com.mcquest.server.playerclass.PlayerClassManager;
import com.mcquest.server.playerclass.PlayerClass;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

/**
 * A Weapon is an Item that can be equipped by a PlayerCharacter to damage
 * enemies.
 */
public class Weapon extends Item {
    private final PlayerClass playerClass;
    private final int level;
    private final double damage;

    /**
     * Constructs a Weapon with the given name, rarity, icon, description,
     * player class, level, and damage.
     */
    Weapon(int id, @NotNull String name, @NotNull ItemRarity rarity,
           @NotNull Material icon, @NotNull String description,
           @NotNull PlayerClass playerClass, int level, double damage) {
        super(id, name, rarity, icon, description);
        this.playerClass = playerClass;
        this.level = level;
        this.damage = damage;
    }

    /**
     * Returns the PlayerClass that can equip this Weapon.
     */
    public final PlayerClass getPlayerClass() {
        return playerClass;
    }

    /**
     * Returns the minimum level required to equip this Weapon.
     */
    public final int getLevel() {
        return level;
    }

    /**
     * Returns how much damage this Weapon inflicts.
     */
    public final double getDamage() {
        return damage;
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
        lore.append(" Weapon\nLevel ");
        lore.append(level);
        lore.append('\n');
        lore.append((int) Math.round(damage));
        lore.append(" Damage");
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
