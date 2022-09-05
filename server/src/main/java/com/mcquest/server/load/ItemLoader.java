package com.mcquest.server.load;

import com.mcquest.server.item.*;
import com.mcquest.server.util.ResourceLoader;

import java.util.List;

public class ItemLoader {
    /**
     * Loads all Items and registers them with the ItemManager.
     */
    public void loadItems(ItemManager itemManager) {
        loadItems("items/armor", ArmorItem.class, itemManager);
        loadItems("items/consumables", ConsumableItem.class, itemManager);
        loadItems("items/misc", Item.class, itemManager);
        loadItems("items/weapons", Weapon.class, itemManager);
    }

    private void loadItems(String resourcesPath, Class<? extends Item> itemType,
                           ItemManager itemManager) {
        List<String> itemPaths = ResourceLoader.getResources(resourcesPath);
        for (String itemPath : itemPaths) {
            Item item = ResourceLoader.deserializeJsonResource(
                    itemPath, itemType);
            itemManager.registerItem(item);
        }
    }
}
