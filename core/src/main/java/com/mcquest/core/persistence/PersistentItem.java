package com.mcquest.core.persistence;

public class PersistentItem {
    private final int itemId;
    private final int amount;
    private final int slot;

    PersistentItem(int itemId, int amount, int slot) {
        this.itemId = itemId;
        this.amount = amount;
        this.slot = slot;
    }

    public int getItemId() {
        return itemId;
    }

    public int getAmount() {
        return amount;
    }

    public int getSlot() {
        return slot;
    }
}
