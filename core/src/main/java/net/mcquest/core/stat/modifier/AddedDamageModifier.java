package net.mcquest.core.stat.modifier;

import net.mcquest.core.damage.DamageType;
import net.mcquest.core.stat.CharacterStats;

public class AddedDamageModifier implements CharacterStatModifier{
    DamageType type;
    double value;

    public AddedDamageModifier(DamageType type, double value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public void activate(CharacterStats stats) {
        double d = stats.baseDamage.get(type);
        stats.baseDamage.put(type, d + value);
    }

    @Override
    public void deactivate(CharacterStats stats) {
        double d = stats.baseDamage.get(type);
        stats.baseDamage.put(type, d - value);
    }

    @Override
    public String toString() {
        return "Adds " + value + " " + type.toString() + " Damage";
    }
}
