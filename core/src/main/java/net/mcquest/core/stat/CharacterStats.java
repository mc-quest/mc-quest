package net.mcquest.core.stat;

import net.mcquest.core.damage.DamageType;
import java.util.Map;

public class CharacterStats {
    private double maxHealth;
    private double health;
    private Map<DamageType, Double> baseDamage;
    private Map<DamageType, Double> increasedDamage;
}
