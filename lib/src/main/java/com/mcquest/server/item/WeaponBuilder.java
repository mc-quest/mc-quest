package com.mcquest.server.item;

import com.mcquest.server.playerclass.PlayerClass;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

public class WeaponBuilder extends ItemBuilder {
    final int level;
    final List<PlayerClass> playerClasses;
    double physicalDamage;

    WeaponBuilder(ItemManager itemManager, int id, String name, ItemRarity rarity,
                  Material icon, int level) {
        super(itemManager, id, name, rarity, icon);
        this.level = level;
        this.playerClasses = new ArrayList<>();
    }

    @Override
    public WeaponBuilder description(String description) {
        return (WeaponBuilder) super.description(description);
    }

    public WeaponBuilder addPlayerClass(PlayerClass playerClass) {
        playerClasses.add(playerClass);
        return this;
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
