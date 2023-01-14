package com.mcquest.server.item;

public enum ArmorType {
    CLOTH("Cloth"),
    LEATHER("Leather"),
    MAIL("Mail"),
    PLATE("Plate");

    private final String text;

    ArmorType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
