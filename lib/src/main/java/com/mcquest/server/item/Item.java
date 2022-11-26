package com.mcquest.server.item;

import com.mcquest.server.instance.Instance;
import com.mcquest.server.util.ItemStackUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.ItemEntity;
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

    Item(Builder builder) {
        id = builder.id;
        name = builder.name;
        rarity = builder.rarity;
        icon = builder.icon;
        description = builder.description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemRarity getRarity() {
        return rarity;
    }

    public Material getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

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

    public static Builder builder(int id, String name, ItemRarity rarity, Material icon) {
        return new Builder(id, name, rarity, icon);
    }

    public static class Builder {
        final int id;
        final String name;
        final ItemRarity rarity;
        final Material icon;
        String description;

        Builder(int id, String name, ItemRarity rarity, Material icon) {
            this.id = id;
            this.name = name;
            this.rarity = rarity;
            this.icon = icon;
            this.description = null;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Item build() {
            return new Item(this);
        }
    }
}
