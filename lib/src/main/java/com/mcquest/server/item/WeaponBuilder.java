package com.mcquest.server.item;

import net.minestom.server.item.Material;

public class WeaponBuilder extends ItemBuilder {
    final int level;
    final WeaponType type;
    double physicalDamage;

    WeaponBuilder(ItemManager itemManager, int id, String name, ItemRarity rarity,
                  Material icon, int level, WeaponType type) {
        super(itemManager, id, name, rarity, icon);
        this.level = level;
        this.type = type;
    }

    @Override
    public WeaponBuilder description(String description) {
        return (WeaponBuilder) super.description(description);
    }

    public WeaponBuilder physicalDamage(double physicalDamage) {
        this.physicalDamage = physicalDamage;
        return this;
    }

    public Weapon build() {
        Weapon weapon = new Weapon(this);
        itemManager.registerItem(weapon);
        return weapon;
    }
}
