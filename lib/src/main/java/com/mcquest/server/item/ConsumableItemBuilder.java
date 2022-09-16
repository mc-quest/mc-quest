package com.mcquest.server.item;

import net.minestom.server.item.Material;

public class ConsumableItemBuilder extends ItemBuilder {
    final int level;

    ConsumableItemBuilder(ItemManager itemManager, int id, String name,
                          ItemRarity rarity, Material icon, int level) {
        super(itemManager, id, name, rarity, icon);
        this.level = level;
    }

    public ConsumableItem build() {
        ConsumableItem consumableItem = new ConsumableItem(this);
        itemManager.registerItem(consumableItem);
        return consumableItem;
    }
}
