package com.mcquest.server.item;

import com.mcquest.server.playerclass.PlayerClass;
import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;

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
    List<Component> getItemStackLore() {
        ItemRarity rarity = getRarity();
        String description = getDescription();
        List<Component> lore = new ArrayList<>();
        lore.add(ItemUtility.rarityText(rarity, "Weapon"));
        lore.add(ItemUtility.playerClassText(playerClasses));
        lore.add(ItemUtility.levelText(level));
        lore.add(Component.empty());
        lore.add(ItemUtility.statText("Damage", damage));
        if (description != null) {
            lore.add(Component.empty());
            lore.addAll(ItemUtility.descriptionText(description));
        }
        lore.add(Component.empty());
        lore.add(ItemUtility.equipText());
        return lore;
    }
}
