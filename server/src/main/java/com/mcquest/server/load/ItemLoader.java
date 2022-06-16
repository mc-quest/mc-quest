package com.mcquest.server.load;

import com.mcquest.server.item.*;
import com.mcquest.server.util.ResourceLoader;

import java.util.List;

public class ItemLoader {
    /**
     * Loads all Items and registers them with the ItemManager.
     */
    public static void loadItems() {
        loadItems("items/armor", ArmorItem.class);
        loadItems("items/consumables", ConsumableItem.class);
        loadItems("items/misc", Item.class);
        loadItems("items/weapons", Weapon.class);
    }

    private static void loadItems(String resourcesPath, Class<? extends Item> itemType) {
        List<String> itemPaths = ResourceLoader.getResources(resourcesPath);
        for (String itemPath : itemPaths) {
            Item item = ResourceLoader.deserializeJsonResource(
                    itemPath, itemType);
            ItemManager.registerItem(item);
        }
    }
}
