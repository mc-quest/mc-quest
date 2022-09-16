package com.mcquest.server.item;

import net.minestom.server.item.Material;

public class ItemBuilder {
    final ItemManager itemManager;
    final int id;
    final String name;
    final ItemRarity rarity;
    final Material icon;
    String description;

    ItemBuilder(ItemManager itemManager, int id, String name, ItemRarity rarity, Material icon) {
        this.itemManager = itemManager;
        this.id = id;
        this.name = name;
        this.rarity = rarity;
        this.icon = icon;
        this.description = null;
    }

    public ItemBuilder description(String description) {
        this.description = description;
        return this;
    }

    public Item build() {
        Item item = new Item(this);
        itemManager.registerItem(item);
        return item;
    }
}
