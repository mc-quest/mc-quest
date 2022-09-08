package com.mcquest.server.item;

import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.util.HashableItemStack;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * The ItemManager is used to register and retrieve Items.
 */
public class ItemManager {
    private final Map<String, Item> itemsByName;
    private final Map<HashableItemStack, Item> itemsByItemStack;

    @ApiStatus.Internal
    public ItemManager() {
        itemsByName = new HashMap<>();
        itemsByItemStack = new HashMap<>();
    }

    /**
     * Returns the Item with the given name, or null if none exists.
     */
    public Item getItem(@NotNull String name) {
        return itemsByName.get(name);
    }

    /**
     * Returns the Item with the given ItemStack, or null if none exists.
     */
    public Item getItem(@NotNull ItemStack itemStack) {
        HashableItemStack key = new HashableItemStack(itemStack.withAmount(1));
        return itemsByItemStack.get(key);
    }

    public Item createItem(@NotNull String name, @NotNull ItemRarity rarity,
                           @NotNull Material icon, @NotNull String description) {
        Item item = new Item(name, rarity, icon, description);
        registerItem(item);
        return item;
    }

    public Weapon createWeapon(@NotNull String name, @NotNull ItemRarity rarity,
                               @NotNull Material icon, @NotNull String description,
                               @NotNull PlayerClass playerClass, int level, double damage) {
        Weapon weapon = new Weapon(name, rarity, icon, description, playerClass, level, damage);
        registerItem(weapon);
        return weapon;
    }

    public ArmorItem createArmorItem(@NotNull String name, @NotNull ItemRarity rarity,
                                     @NotNull Material icon, @NotNull String description,
                                     @NotNull PlayerClass playerClass, int level,
                                     @NotNull ArmorSlot slot, double protections) {
        ArmorItem armorItem = new ArmorItem(name, rarity, icon, description, playerClass, level, slot, protections);
        registerItem(armorItem);
        return armorItem;
    }

    private void registerItem(@NotNull Item item) {
        String name = item.getName();
        if (itemsByName.containsKey(name)) {
            throw new IllegalArgumentException("Name already in use: " + name);
        }

        ItemStack itemStack = item.getItemStack();
        HashableItemStack itemStackKey = new HashableItemStack(itemStack);
        if (itemsByItemStack.containsKey(itemStackKey)) {
            throw new IllegalArgumentException("ItemStack already in use: " + name);
        }

        itemsByName.put(item.getName(), item);
        itemsByItemStack.put(itemStackKey, item);
    }
}
