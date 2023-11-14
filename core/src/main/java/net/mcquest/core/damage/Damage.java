package net.mcquest.core.damage;

import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.playerclass.ActiveSkill;

import java.util.EnumMap;
import java.util.Map;

public class Damage {
    public static double calculate(PlayerCharacter pc1, PlayerCharacter pc2, ActiveSkill skill) {
        return 0.0;
    }

    private static class Builder {
        private double damageEffectiveness;
        private DamageType type;
        private Map<DamageType, Double> baseDamage;
        private Map<DamageType, Double> increasedDamage;

        public Builder() {
            baseDamage = new EnumMap<>(DamageType.class);
            increasedDamage = new EnumMap<>(DamageType.class);
            for (DamageType type : DamageType.values()) {
                baseDamage.put(type, 0.0);
                increasedDamage.put(type, 0.0);
            }
        }

        public Builder damageEffectiveness(double damageEffectiveness) {
            this.damageEffectiveness = damageEffectiveness;
            return this;
        }

        public Builder type(DamageType type) {
            this.type = type;
            return this;
        }

        public Builder baseDamage(DamageType type, double amount) {
            baseDamage.put(type, baseDamage.get(type) + amount);
            return this;
        }

        public Builder increasedDamage(DamageType type, double amount) {
            increasedDamage.put(type, increasedDamage.get(type) + amount);
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

            // Return damage amount
            double sum = 0;
            for (double d : damage.values()) {
                sum += d;
            }
            return sum;
        }
    }
}
