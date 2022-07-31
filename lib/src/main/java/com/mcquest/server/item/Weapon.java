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
    private final String playerClass;
    private final int level;
    private final double damage;

    /**
     * Constructs a Weapon with the given name, rarity, icon, description,
     * player class, level, and damage.
     */
    public Weapon(String name, ItemRarity rarity, Material icon,
                  String description, String playerClass, int level,
                  double damage) {
        super(name, rarity, icon, description);
        this.playerClass = playerClass;
        this.level = level;
        this.damage = damage;
    }

    /**
     * Constructs a Weapon with the given name, rarity, icon, description,
     * PlayerClass, level, and damage.
     */
    public Weapon(String name, ItemRarity rarity, Material icon,
                  String description, PlayerClass playerClass, int level,
                  double damage) {
        this(name, rarity, icon, description, playerClass.getName(), level,
                damage);
    }

    /**
     * Constructs a Weapon with the given name, rarity, icon, player class,
     * level, and damage. The Weapon will have no description.
     */
    public Weapon(String name, ItemRarity rarity, Material icon,
                  String playerClass, int level, double damage) {
        this(name, rarity, icon, null, playerClass, level, damage);
    }

    /**
     * Constructs a Weapon with the given name, rarity, icon, PlayerClass,
     * level, and damage. The Weapon will have no description.
     */
    public Weapon(String name, ItemRarity rarity, Material icon,
                  PlayerClass playerClass, int level, double damage) {
        this(name, rarity, icon, null, playerClass, level, damage);
    }

    /**
     * Returns the PlayerClass that can equip this Weapon.
     */
    public final PlayerClass getPlayerClass() {
        return PlayerClassManager.getPlayerClass(playerClass);
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
