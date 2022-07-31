package com.mcquest.server.util;

import net.minestom.server.item.ItemStack;

import java.util.Objects;

/**
 * A wrapper around ItemStack that can be used as keys in a HashMap.
 */
public class HashableItemStack {
    private final ItemStack itemStack;

    public HashableItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        return (obj instanceof HashableItemStack other)
                && (this.itemStack.isSimilar(other.itemStack))
                && (this.itemStack.amount() == other.itemStack.amount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemStack.material(), itemStack.meta(), itemStack.amount());
    }
}
