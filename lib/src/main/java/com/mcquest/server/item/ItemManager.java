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

    public ItemBuilder itemBuilder(int id, @NotNull String name, @NotNull ItemRarity rarity,
                                   @NotNull Material icon) {
        return new ItemBuilder(this, id, name, rarity, icon);
    }

    public WeaponBuilder weaponBuilder(int id, @NotNull String name, @NotNull ItemRarity rarity,
                                       @NotNull Material icon, int level, double damage) {
        return new WeaponBuilder(this, id, name, rarity, icon, level, damage);
    }

    public ArmorItemBuilder armorItemBuilder(int id, @NotNull String name, @NotNull ItemRarity rarity,
                                             @NotNull Material icon, int level,
                                             @NotNull ArmorSlot slot, double protections) {
        return new ArmorItemBuilder(this, id, name, rarity, icon, level, slot, protections);
    }

    public ConsumableItemBuilder consumableItemBuilder(int id, @NotNull String name,
                                                       @NotNull ItemRarity rarity,
                                                       @NotNull Material icon, int level) {
        return new ConsumableItemBuilder(this, id, name, rarity, icon, level);
    }

    void registerItem(@NotNull Item item) {
        int id = item.getId();
        if (itemsById.containsKey(id)) {
            throw new IllegalArgumentException("ID of " + item.getName() + " is already in use");
        }
        itemsById.put(id, item);
    }
}
