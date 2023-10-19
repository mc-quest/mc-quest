package net.mcquest.core.persistence;

public class PersistentItem {
    private final String itemId;
    private final int amount;
    private final int slot;

    PersistentItem(String itemId, int amount, int slot) {
        this.itemId = itemId;
        this.amount = amount;
        this.slot = slot;
    }

    public String getItemId() {
        return itemId;
    }

    public int getAmount() {
        return amount;
    }

    public int getSlot() {
        return slot;
    }
}
