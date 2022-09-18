package com.mcquest.server.item;

import com.mcquest.server.playerclass.PlayerClass;
import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

public class ArmorItemBuilder extends ItemBuilder {
    final List<PlayerClass> playerClasses;
    final int level;
    final ArmorSlot slot;
    final double protections;

    ArmorItemBuilder(ItemManager itemManager, int id, String name, ItemRarity rarity,
                     Material icon, int level, ArmorSlot slot, double protections) {
        super(itemManager, id, name, rarity, icon);
        this.level = level;
        this.protections = protections;
        this.slot = slot;
        this.playerClasses = new ArrayList<>();
    }

    @Override
    public ArmorItemBuilder description(String description) {
        return (ArmorItemBuilder) super.description(description);
    }

    public ArmorItemBuilder playerClass(PlayerClass playerClass) {
        playerClasses.add(playerClass);
        return this;
    }

    public ArmorItem build() {
        ArmorItem armorItem = new ArmorItem(this);
        itemManager.registerItem(armorItem);
        return armorItem;
    }
}
