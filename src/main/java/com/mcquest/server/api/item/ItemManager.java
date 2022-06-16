package com.mcquest.server.api.item;

import net.minestom.server.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * The ItemManager is used to register and retrieve Items.
 */
public class ItemManager {
    private static final Map<String, Item> itemsByName = new HashMap<>();
    private static final Map<ItemStack, Item> itemsByItemStack = new HashMap<>();

    /**
     * Registers an Item with the MMORPG.
     */
    public static void registerItem(Item item) {
        Objects.requireNonNull(item);
        String name = item.getName();
        if (itemsByName.containsKey(name)) {
            throw new IllegalArgumentException("Attempted to register an item "
                    + "with a name that is already registered: " + name);
        }
        ItemStack itemStack = item.getItemStack();
        if (itemsByItemStack.containsKey(itemStack)) {
            throw new IllegalArgumentException("Attempted to register an item "
                    + "with an ItemStack that is already registered: " + name);
        }
        itemsByName.put(item.getName(), item);
        itemsByItemStack.put(item.getItemStack(), item);
    }

    /**
     * Returns the Item with the given name, or null if none exists.
     */
    public static Item getItem(String name) {
        return itemsByName.get(Objects.requireNonNull(name));
    }

    /**
     * Returns the Item with the given ItemStack, or null if none exists.
     */
    public static Item getItem(ItemStack itemStack) {
        return itemsByItemStack.get(Objects.requireNonNull(itemStack));
    }
}
