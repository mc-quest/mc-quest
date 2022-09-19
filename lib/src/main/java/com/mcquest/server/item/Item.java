package com.mcquest.server.item;

import com.mcquest.server.util.ItemStackUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.instance.Instance;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Item {
    private final int id;
    private final String name;
    private final ItemRarity rarity;
    private final Material icon;
    private final String description;
    private ItemStack itemStack;

    Item(ItemBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.rarity = builder.rarity;
        this.icon = builder.icon;
        this.description = builder.description;
    }

    public int getId() {
        return id;
    }

    /**
     * Returns the name of this Item.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the rarity of this Item.
     */
    public ItemRarity getRarity() {
        return rarity;
    }

    /**
     * Returns the icon of this Item.
     */
    public Material getIcon() {
        return icon;
    }

    /**
     * Returns the description of this Item.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the ItemStack of this Item.
     */
    public ItemStack getItemStack() {
        // Lazy initialize because createItemStack() relies on subclass fields.
        if (itemStack == null) {
            itemStack = createItemStack();
        }
        return itemStack;
    }

    public TextComponent getDisplayName() {
        return Component.text(name, rarity.getColor());
    }

    private ItemStack createItemStack() {
        return ItemStackUtility.createItemStack(icon, getDisplayName(), getItemStackLore())
                .withTag(ItemManager.ID_TAG, id);
    }

    List<Component> getItemStackLore() {
        List<Component> lore = new ArrayList<>();
        lore.add(ItemUtility.rarityText(rarity, "Item"));
        if (description != null) {
            lore.add(Component.empty());
            lore.addAll(ItemUtility.descriptionText(description));
        }
        return lore;
    }

    public void drop(Instance instance, Pos position) {
        drop(instance, position, 1);
    }

    public void drop(@NotNull Instance instance, @NotNull Pos position, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("amount < 0");
        }
        ItemEntity drop = new ItemEntity(itemStack.withAmount(amount));
        drop.setCustomName(getDisplayName());
        drop.setCustomNameVisible(true);
        drop.setInstance(instance, position);
    }
}
