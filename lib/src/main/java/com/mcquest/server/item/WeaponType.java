package com.mcquest.server.item;

public enum WeaponType {
    AXE("Axe"),
    BOW("Bow"),
    DAGGER("Dagger"),
    MACE("Mace"),
    POLEARM("Polearm"),
    STAFF("Staff"),
    SWORD("Sword"),
    WAND("Wand");

    private final String text;

    WeaponType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
