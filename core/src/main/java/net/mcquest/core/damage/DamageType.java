package net.mcquest.core.damage;

public enum DamageType {
    Physical("Physical"),
    Lightning("Lightning"),
    Cold("Cold"),
    Fire("Fire"),
    Generic("");

    private final String text;

    DamageType(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
