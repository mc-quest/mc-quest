package net.mcquest.core.damage;

import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.playerclass.ActiveSkill;
import net.mcquest.core.stat.AttackStats;
import net.mcquest.core.stat.CharacterStats;

import java.util.EnumMap;
import java.util.Map;

public class Damage {
    public static double calculate(CharacterStats attackerStats, CharacterStats defenderStats, AttackStats attackStats) {
        Builder builder = new Builder()
                .type(attackStats.type)
                .damageEffectiveness(attackStats.damageEffectiveness)
                .protection(defenderStats.protection);
        for (DamageType type : DamageType.values()) {
            builder.baseDamage(type, attackerStats.baseDamage.get(type))
                    .increasedDamage(type, attackerStats.increasedDamage.get(type));
        }

        return builder.calculate();
    }

    private static class Builder {
        private DamageType type;
        private double damageEffectiveness;
        private Map<DamageType, Double> baseDamage;
        private Map<DamageType, Double> increasedDamage;
        private double protection;

        public Builder() {
            baseDamage = new EnumMap<>(DamageType.class);
            increasedDamage = new EnumMap<>(DamageType.class);
            for (DamageType type : DamageType.values()) {
                baseDamage.put(type, 0.0);
                increasedDamage.put(type, 0.0);
            }
        }

        public Builder type(DamageType type) {
            this.type = type;
            return this;
        }

        public Builder damageEffectiveness(double value) {
            this.damageEffectiveness = value;
            return this;
        }

        public Builder baseDamage(DamageType type, double value) {
            baseDamage.put(type, baseDamage.get(type) + value);
            return this;
        }

        public Builder increasedDamage(DamageType type, double value) {
            increasedDamage.put(type, increasedDamage.get(type) + value);
            return this;
        }

        public Builder protection(double value) {
            protection = value;
            return this;
        }

        public double calculate() {
            EnumMap<DamageType, Double> damage = new EnumMap<>(DamageType.class);
            for (DamageType type : DamageType.values()) {
                damage.put(type, 0.0);
            }

            // Calculate base damage
            for (DamageType type : DamageType.values()) {
                if (type == DamageType.Generic) {
                    double d = damage.get(this.type) + baseDamage.get(DamageType.Generic);
                    damage.put(this.type, d);
                } else {
                    double d = damage.get(type) + baseDamage.get(type);
                    damage.put(type, d);
                }
            }

            // Calculate damage effectiveness
            for (DamageType type : DamageType.values()) {
                double d = damage.get(type) * damageEffectiveness;
                damage.put(type, d);
            }

            // Calculate increased damage
            for (DamageType type : DamageType.values()) {
                if (type != DamageType.Generic) {
                    double i = increasedDamage.get(type) + increasedDamage.get(DamageType.Generic);
                    double d = damage.get(type) * (i + 1.0);
                    damage.put(type, d);
                }
            }

            // Calculate protection
            {
                double d = damage.get(DamageType.Physical);
                if (d != 0) {
                    d = (5 * d * d) / (protection + 5 * d);
                    damage.put(DamageType.Physical, d);
                }
            }

            // Return damage amount
            double sum = 0;
            for (double d : damage.values()) {
                sum += d;
            }
            return sum;
        }
    }
}
