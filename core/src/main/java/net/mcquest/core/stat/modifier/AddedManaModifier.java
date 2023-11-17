package net.mcquest.core.stat.modifier;

import net.mcquest.core.stat.CharacterStats;

public class AddedManaModifier implements CharacterStatModifier {
    double value;

    public AddedManaModifier(double value) {
        this.value = value;
    }

    @Override
    public void activate(CharacterStats stats) {
        stats.maxMana += value;
        stats.mana += value;
    }

    @Override
    public void deactivate(CharacterStats stats) {
        stats.maxMana -= value;
        stats.mana -= value;
    }

    @Override
    public String toString() {
        return "Adds " + value + " Mana";
    }
}