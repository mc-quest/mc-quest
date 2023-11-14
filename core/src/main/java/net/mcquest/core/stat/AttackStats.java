package net.mcquest.core.stat;

import net.mcquest.core.damage.DamageType;

public class AttackStats {
    private DamageType type;
    private double damageEffectiveness;

    public AttackStats(DamageType type, double damageEffectiveness) {
        this.type = type;
        this.damageEffectiveness = damageEffectiveness;
    }
}
