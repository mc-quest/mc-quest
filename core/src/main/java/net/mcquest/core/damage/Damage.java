package net.mcquest.core.damage;

import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.playerclass.ActiveSkill;
import net.mcquest.core.playerclass.PlayerClass;

import java.util.Arrays;
import java.util.Map;

public class Damage {
    public static double calculate(PlayerCharacter pc1, PlayerCharacter pc2, ActiveSkill skill) {
        return 0.0;
    }

    private static class Builder {
        private double damageEffectiveness;
        private DamageType type;
        private double[] baseDamage;
        private double[] increasedDamage;

        public Builder() {
            double[] baseDamage = new double[DamageType.NUM_DAMAGE_TYPES];
            double[] increasedDamage = new double[DamageType.NUM_DAMAGE_TYPES];
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
            baseDamage[type.getId()] += amount;
            return this;
        }

        public Builder increasedDamage(DamageType type, double amount) {
            increasedDamage[type.getId()] += amount;
            return this;
        }

        public double calculate() {
            double[] damage = new double[DamageType.NUM_DAMAGE_TYPES - 1];

            // Calculate base damage
            damage[type.getId()] += baseDamage[DamageType.Generic.getId()];
            for (int i = 0; i < damage.length; i++) {
                damage[i] += baseDamage[i];
            }

            // Calculate increased damage
            for (int i = 0; i < damage.length; i++) {
                double increased = increasedDamage[DamageType.Generic.getId()] + increasedDamage[i];
                damage[i] *= increased;
            }

            // Return damage amount
            double sum = 0;
            for (double d : damage) {
                sum += d;
            }
            return sum;
        }
    }
}
