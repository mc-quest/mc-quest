package com.mcquest.server.item;

import com.mcquest.server.instance.Instance;
import com.mcquest.server.util.ItemStackUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.file.FileTree;

import java.util.List;

public abstract class Item {
    private final int id;
    private final String name;
    private final ItemQuality quality;
    private final String description;
    private ItemStack itemStack;

    Item(int id, String name, ItemQuality quality, String description) {
        this.id = id;
        this.name = name;
        this.quality = quality;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ItemQuality getQuality() {
        return quality;
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
        return Component.text(name, quality.getColor());
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

    private ItemStack createItemStack() {
        return ItemStackUtility.createItemStack(Material.WOODEN_AXE, getDisplayName(), getItemStackLore())
                .withTag(ItemManager.ID_TAG, id);
    }

    abstract List<Component> getItemStackLore();

    @ApiStatus.Internal
    public abstract void writeResources(FileTree tree);
}
