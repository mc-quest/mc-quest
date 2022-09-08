package com.mcquest.server.item;

public enum ArmorSlot {
    FEET("Feet"), LEGS("Legs"), CHEST("Chest"), HEAD("Head");

    private final String text;

    ArmorSlot(String text) {
        this.text = text;
    }

    /**
     * Returns the text that describes this Slot.
     */
    public String getText() {
        return text;
    }
}
