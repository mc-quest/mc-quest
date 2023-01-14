package com.mcquest.server.persistence;

public class PersistentItem {
    private final int itemId;
    private final int amount;
    private final int inventorySlot;

    PersistentItem(int itemId, int amount, int inventorySlot) {
        this.itemId = itemId;
        this.amount = amount;
        this.inventorySlot = inventorySlot;
    }

    public int getItemId() {
        return itemId;
    }

    public int getAmount() {
        return amount;
    }

    public int getInventorySlot() {
        return inventorySlot;
    }
}
