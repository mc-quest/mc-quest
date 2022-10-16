package com.mcquest.server.item;

import net.minestom.server.item.Material;

public class ArmorItemBuilder extends ItemBuilder {
    final int level;
    final ArmorType type;
    final ArmorSlot slot;
    final double protections;

    ArmorItemBuilder(ItemManager itemManager, int id, String name,
                     ItemRarity rarity, Material icon, int level,
                     ArmorType type, ArmorSlot slot, double protections) {
        super(itemManager, id, name, rarity, icon);
        this.level = level;
        this.protections = protections;
        this.type = type;
        this.slot = slot;
    }

    @Override
    public ArmorItemBuilder description(String description) {
        return (ArmorItemBuilder) super.description(description);
    }

    public ArmorItem build() {
        ArmorItem armorItem = new ArmorItem(this);
        itemManager.registerItem(armorItem);
        return armorItem;
    }
}
