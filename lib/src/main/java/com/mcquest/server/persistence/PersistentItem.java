package com.mcquest.server.persistence;

public class PersistentItem {
    private final int itemId;
    private final int amount;

    public PersistentItem(int itemId, int amount) {
        this.itemId = itemId;
        this.amount = amount;
    }

    public int getItemId() {
        return itemId;
    }

    public int getAmount() {
        return amount;
    }
}
