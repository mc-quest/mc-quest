package net.mcquest.core.damage;

public enum DamageType {
    Physical("Physical", 0),
    Lightning("Lightning", 1),
    Cold("Cold", 2),
    Fire("Fire", 3),
    Generic("", 4);

    public static final int NUM_DAMAGE_TYPES = 5;

    private final String text;
    private final int id;

    DamageType(String text, int id) {
        this.text = text;
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public int getId() {
        return id;
    }
}
