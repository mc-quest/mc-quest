package net.mcquest.core.item;

public enum ArmorSlot {
    FEET("Feet"), LEGS("Legs"), CHEST("Chest"), HEAD("Head");

    private final String text;

    ArmorSlot(String text) {
        this.text = text;
    }

    /**
     * Returns the text that describes this ArmorSlot.
     */
    public String getText() {
        return text;
    }
}
