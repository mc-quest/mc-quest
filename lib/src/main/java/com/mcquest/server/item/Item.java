package com.mcquest.server.item;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.Objects;

/**
 * An Item represents an MMORPG item.
 */
public class Item {
    private final String name;
    private final ItemRarity rarity;
    private final Material icon;
    private final String description;
    private transient ItemStack itemStack;

    /**
     * Constructs an Item with the given name, rarity, icon, and description.
     */
    public Item(String name, ItemRarity rarity, Material icon,
                String description) {
        this.name = Objects.requireNonNull(name);
        this.rarity = Objects.requireNonNull(rarity);
        this.icon = Objects.requireNonNull(icon);
        this.description = Objects.requireNonNull(description);
    }

    /**
     * Constructs an Item with the given name, rarity, icon, and no
     * description.
     */
    public Item(String name, ItemRarity rarity, Material icon) {
        this(name, rarity, icon, null);
    }

    /**
     * Returns the name of this Item.
     */
    public final String getName() {
        return name;
    }

    /**
     * Returns the rarity of this Item.
     */
    public final ItemRarity getRarity() {
        return rarity;
    }

    /**
     * Returns the icon of this Item.
     */
    public final Material getIcon() {
        return icon;
    }

    /**
     * Returns the description of this Item.
     */
    public final String getDescription() {
        return description;
    }

    /**
     * Returns the ItemStack of this Item.
     */
    public final ItemStack getItemStack() {
        if (itemStack == null) {
            itemStack = createItemStack();
        }
        return itemStack;
    }

    public final Component getDisplayName() {
        return Component.text(name, rarity.getColor());
    }

    /**
     * Constructs the ItemStack of this Item. This method can be overridden to
     * add custom properties to the ItemStack.
     */
    protected ItemStack createItemStack() {
        // TODO
        Component rarityText = Component.text(rarity.getText() + " Item",
                rarity.getColor());

        if (description != null) {
            // TODO
        }

        return ItemStack.builder(Material.AIR)
                .displayName(getDisplayName())
                .lore(rarityText) // TODO: add description
                .build();
    }

    public void drop(Instance instance, Pos position) {
        drop(instance, position, 1);
    }

    public void drop(Instance instance, Pos position, int amount) {
        // TODO: Test this.
        ItemEntity drop = new ItemEntity(itemStack.withAmount(amount));
        drop.setInstance(Objects.requireNonNull(instance),
                Objects.requireNonNull(position));
    }
}
