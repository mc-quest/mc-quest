package net.mcquest.core.stat.modifier;

import net.mcquest.core.stat.CharacterStats;

public class AddedProtectionModifier implements CharacterStatModifier {
    double value;

    public AddedProtectionModifier(double value) {
        this.value = value;
    }

    @Override
    public void activate(CharacterStats stats) {
        stats.protection += value;
    }

    @Override
    public void deactivate(CharacterStats stats) {
        stats.protection -= value;
    }

    @Override
    public String toString() {
        return "Adds " + value + " Protection";
    }
}