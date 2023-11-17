package net.mcquest.core.stat.modifier;

import net.mcquest.core.stat.CharacterStats;

public class AddedHealthModifier implements CharacterStatModifier {
    double value;

    public AddedHealthModifier(double value) {
        this.value = value;
    }

    @Override
    public void activate(CharacterStats stats) {
        stats.maxHealth += value;
        stats.health += value;
    }

    @Override
    public void deactivate(CharacterStats stats) {
        stats.maxHealth -= value;
        stats.health -= value;
    }

    @Override
    public String toString() {
        return "Adds " + value + " Health";
    }
}
