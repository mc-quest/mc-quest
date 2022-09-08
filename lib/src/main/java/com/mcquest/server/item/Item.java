package com.mcquest.server.item;

import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemHideFlag;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

/**
 * An Item represents an MMORPG item.
 */
public class Item {
    private final String name;
    private final ItemRarity rarity;
    private final String icon;
    private final String description;
    private final ItemStack itemStack;

    /**
     * Constructs an Item with the given name, rarity, icon, and description.
     */
    Item(@NotNull String name, @NotNull ItemRarity rarity,
         @NotNull Material icon, @NotNull String description) {
        this.name = name;
        this.rarity = rarity;
        this.icon = icon.namespace().asString();
        this.description = description;
        this.itemStack = createItemStack();
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
        return Material.fromNamespaceId(icon);
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
        return itemStack;
    }

    public final Component getDisplayName() {
        return Component.text(name, rarity.getColor());
    }

    /**
     * Constructs the ItemStack of this Item. This method can be overridden to
     * add custom properties to the ItemStack. The amount of the returned
     * ItemStack must be exactly 1.
     */
    @NotNull ItemStack createItemStack() {
        Component rarityText = Component.text(rarity.getText() + " Item", rarity.getColor());

        if (description != null) {
            // TODO
        }

        return ItemStack.builder(getIcon())
                .meta(builder ->
                        builder.hideFlag(ItemHideFlag.HIDE_ATTRIBUTES, ItemHideFlag.HIDE_POTION_EFFECTS)
                )
                .displayName(getDisplayName())
                .lore(rarityText) // TODO: add description
                .build();
    }

    public void drop(Instance instance, Pos position) {
        drop(instance, position, 1);
    }

    public void drop(@NotNull Instance instance, @NotNull Pos position,
                     int amount) {
        // TODO: Test this.
        if (amount < 0) {
            throw new IllegalArgumentException("amount < 0");
        }
        ItemEntity drop = new ItemEntity(getItemStack().withAmount(amount));
        drop.setCustomName(getDisplayName());
        drop.setCustomNameVisible(true);
        drop.setInstance(instance, position);
    }
}
