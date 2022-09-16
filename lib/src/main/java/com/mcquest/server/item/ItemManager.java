package com.mcquest.server.item;

import com.mcquest.server.playerclass.PlayerClass;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * The ItemManager is used to register and retrieve Items.
 */
public class ItemManager {
    static final Tag<Integer> ID_TAG = Tag.Integer("id");

    private final Map<Integer, Item> itemsById;

    @ApiStatus.Internal
    public ItemManager() {
        itemsById = new HashMap<>();
    }

    /**
     * Returns the Item with the given ID, or null if none exists.
     */
    public Item getItem(int id) {
        return itemsById.get(id);
    }

    /**
     * Returns the Item with the given ItemStack, or null if none exists.
     */
    public Item getItem(@NotNull ItemStack itemStack) {
        if (!itemStack.hasTag(ID_TAG)) {
            return null;
        }
        int id = itemStack.getTag(ID_TAG);
        return itemsById.get(id);
    }

    public Item createItem(int id, @NotNull String name, @NotNull ItemRarity rarity,
                           @NotNull Material icon, @NotNull String description) {
        Item item = new Item(id, name, rarity, icon, description);
        registerItem(item);
        return item;
    }

    public Weapon createWeapon(int id, @NotNull String name, @NotNull ItemRarity rarity,
                               @NotNull Material icon, @NotNull String description,
                               @NotNull PlayerClass playerClass, int level, double damage) {
        Weapon weapon = new Weapon(id, name, rarity, icon, description, playerClass, level, damage);
        registerItem(weapon);
        return weapon;
    }

    public ArmorItem createArmorItem(int id, @NotNull String name, @NotNull ItemRarity rarity,
                                     @NotNull Material icon, @NotNull String description,
                                     @NotNull PlayerClass playerClass, int level,
                                     @NotNull ArmorSlot slot, double protections) {
        ArmorItem armorItem = new ArmorItem(id, name, rarity, icon, description, playerClass,
                level, slot, protections);
        registerItem(armorItem);
        return armorItem;
    }

    void registerItem(@NotNull Item item) {
        int id = item.getId();
        if (itemsById.containsKey(id)) {
            throw new IllegalArgumentException("ID of " + item.getName() + " is already in use");
        }
        itemsById.put(id, item);
    }
}
