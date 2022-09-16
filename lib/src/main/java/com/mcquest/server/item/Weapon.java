package com.mcquest.server.item;

import com.mcquest.server.playerclass.PlayerClass;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

/**
 * A Weapon is an Item that can be equipped by a PlayerCharacter to damage
 * enemies.
 */
public class Weapon extends Item {
    private final PlayerClass[] playerClasses;
    private final int level;
    private final double damage;

    Weapon(WeaponBuilder builder) {
        super(builder);
        this.playerClasses = builder.playerClasses.toArray(new PlayerClass[0]);
        this.level = builder.level;
        this.damage = builder.damage;
    }

    public int getPlayerClassCount() {
        return playerClasses.length;
    }

    public PlayerClass getPlayerClass(int index) {
        return playerClasses[index];
    }

    /**
     * Returns the minimum level required to equip this Weapon.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns how much damage this Weapon inflicts.
     */
    public double getDamage() {
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
        // lore.append(playerClass);
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
