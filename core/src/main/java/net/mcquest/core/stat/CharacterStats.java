package net.mcquest.core.stat;

import net.mcquest.core.damage.DamageType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class CharacterStats {
    public double maxHealth;
    public double health;
    public double maxMana;
    public double mana;
    public double healthRegenRate;
    public double manaRegenRate;
    public double protection;
    public Map<DamageType, Double> baseDamage;
    public Map<DamageType, Double> increasedDamage;

    public CharacterStats() {
        maxHealth = 0.0;
        health = 0.0;
        maxMana = 0.0;
        mana = 0.0;
        healthRegenRate = 0.0;
        manaRegenRate = 0.0;
        protection = 0.0;
        baseDamage = new EnumMap<>(DamageType.class);
        increasedDamage = new EnumMap<>(DamageType.class);
        for (DamageType type : DamageType.values()) {
            baseDamage.put(type, 0.0);
            increasedDamage.put(type, 0.0);
        }
    }
}
