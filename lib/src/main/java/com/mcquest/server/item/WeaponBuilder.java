package com.mcquest.server.item;

import com.mcquest.server.playerclass.PlayerClass;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

public class WeaponBuilder extends ItemBuilder {
    final int level;
    final double damage;
    final List<PlayerClass> playerClasses;

    WeaponBuilder(ItemManager itemManager, int id, String name, ItemRarity rarity,
                  Material icon, int level, double damage) {
        super(itemManager, id, name, rarity, icon);
        this.level = level;
        this.damage = damage;
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

    public Weapon build() {
        Weapon weapon = new Weapon(this);
        itemManager.registerItem(weapon);
        return weapon;
    }
}
