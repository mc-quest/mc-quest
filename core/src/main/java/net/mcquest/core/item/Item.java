package net.mcquest.core.item;

import com.google.common.collect.ListMultimap;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.util.MathUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.ItemEntity;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.ItemOverride;

public abstract class Item {
    static final Tag<Integer> ID_TAG = Tag.Integer("item_id");

    private final int id;
    private final String name;
    private final ItemQuality quality;
    private final String description;

    Item(Builder builder) {
        id = builder.id;
        name = builder.name;
        quality = builder.quality;
        description = builder.description;
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
        ItemStack itemStack = getItemStack();
        ItemEntity drop = new ItemEntity(itemStack.withAmount(amount));
        drop.setCustomName(getDisplayName());
        drop.setCustomNameVisible(true);
        drop.setInstance(instance, position);
        drop.setVelocity(new Vec(randomSpeed(), 5.0, randomSpeed()));
    }

    private double randomSpeed() {
        return MathUtility.randomRange(-1.0, 1.0);
    }

    public abstract int getStackSize();

    public abstract ItemStack getItemStack();

    @ApiStatus.Internal
    public abstract void writeResources(FileTree tree,
                                        ListMultimap<Material, ItemOverride> overrides);

    static class Builder {
        int id;
        String name;
        ItemQuality quality;
        String description;
    }
}
