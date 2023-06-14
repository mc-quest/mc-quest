package com.mcquest.server.item;

import net.minestom.server.item.ItemStack;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ItemManager {
    static final Tag<Integer> ID_TAG = Tag.Integer("item_id");

    private final Map<Integer, Item> itemsById;

    @ApiStatus.Internal
    public ItemManager(Item[] items) {
        itemsById = new HashMap<>();
        for (Item item : items) {
            registerItem(item);
        }
    }

    private void registerItem(Item item) {
        int id = item.getId();
        if (itemsById.containsKey(id)) {
            throw new IllegalArgumentException("ID already in use: " + id);
        }
        itemsById.put(id, item);
    }

    /**
     * Returns the Item with the given ID, or null if none exists.
     */
    public @Nullable Item getItem(int id) {
        return itemsById.get(id);
    }

    /**
     * Returns the Item with the given ItemStack, or null if none exists.
     */
    public @Nullable Item getItem(@NotNull ItemStack itemStack) {
        if (!itemStack.hasTag(ID_TAG)) {
            return null;
        }
        int id = itemStack.getTag(ID_TAG);
        return itemsById.get(id);
    }
}
