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
    private final int level;
    private WeaponType type;
    private final double physicalDamage;

    Weapon(WeaponBuilder builder) {
        super(builder);
        this.level = builder.level;
        this.type = builder.type;
        this.physicalDamage = builder.physicalDamage;
    }

    public WeaponType getType() {
        return type;
    }

    /**
     * Returns the minimum level required to equip this Weapon.
     */
    public int getLevel() {
        return level;
    }

    public double getPhysicalDamage() {
        return physicalDamage;
    }

    @Override
    List<Component> getItemStackLore() {
        ItemRarity rarity = getRarity();
        String description = getDescription();
        List<Component> lore = new ArrayList<>();
        lore.add(ItemUtility.rarityText(rarity, type.getText()));
        lore.add(ItemUtility.levelText(level));
        lore.add(Component.empty());
        lore.add(ItemUtility.statText("Physical Damage", physicalDamage));
        if (description != null) {
            lore.add(Component.empty());
            lore.addAll(ItemUtility.descriptionText(description));
        }
        lore.add(Component.empty());
        lore.add(ItemUtility.equipText());
        return lore;
    }
}
