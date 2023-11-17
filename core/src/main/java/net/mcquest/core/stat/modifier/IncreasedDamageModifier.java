package net.mcquest.core.stat.modifier;

import net.mcquest.core.damage.DamageType;
import net.mcquest.core.stat.CharacterStats;

public class IncreasedDamageModifier implements CharacterStatModifier{
    DamageType type;
    double value;

    public IncreasedDamageModifier(DamageType type, double value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public void activate(CharacterStats stats) {
        double d = stats.increasedDamage.get(type);
        stats.increasedDamage.put(type, d + value);
    }

    @Override
    public void deactivate(CharacterStats stats) {
        double d = stats.increasedDamage.get(type);
        stats.increasedDamage.put(type, d - value);
    }

    @Override
    public String toString() {
        if (type == DamageType.Generic) {
            return value + " Increased Damage";
        } else {
            return value + " Increased " + type.toString() + " Damage";
        }
    }
}