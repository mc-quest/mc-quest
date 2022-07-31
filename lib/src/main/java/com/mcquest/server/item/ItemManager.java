package com.mcquest.server.item;

import com.mcquest.server.util.HashableItemStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The ItemManager is used to register and retrieve Items.
 */
public class ItemManager {
    private static final Map<String, Item> itemsByName = new HashMap<>();
    private static final Map<HashableItemStack, Item> itemsByItemStack = new HashMap<>();

    /**
     * Registers an Item with the MMORPG.
     */
    public static void registerItem(@NotNull Item item) {
        Objects.requireNonNull(item);
        String name = item.getName();
        if (itemsByName.containsKey(name)) {
            throw new IllegalArgumentException("Attempted to register an item "
                    + "with a name that is already registered: " + name);
        }

        ItemStack itemStack = item.getItemStack();
        HashableItemStack itemStackKey = new HashableItemStack(itemStack);
        if (itemsByItemStack.containsKey(itemStackKey)) {
            throw new IllegalArgumentException("Attempted to register an item "
                    + "with an ItemStack that is already registered: " + name);
        }

        itemsByName.put(item.getName(), item);
        itemsByItemStack.put(itemStackKey, item);
    }

    /**
     * Returns the Item with the given name, or null if none exists.
     */
    public static Item getItem(@NotNull String name) {
        return itemsByName.get(name);
    }

    /**
     * Returns the Item with the given ItemStack, or null if none exists.
     */
    public static Item getItem(@NotNull ItemStack itemStack) {
        HashableItemStack key = new HashableItemStack(itemStack.withAmount(1));
        return itemsByItemStack.get(key);
    }
}
