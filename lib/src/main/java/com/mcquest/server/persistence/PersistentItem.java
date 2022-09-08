package com.mcquest.server.persistence;

public class PersistentItem {
    private final String name;
    private final int amount;

    public PersistentItem(String name, int amount) {
        this.name = name;
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }
}
