package com.mcquest.core.character;

import com.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.NotNull;

public class NonPlayerCharacter extends Character {
    public NonPlayerCharacter(@NotNull Instance instance, @NotNull Pos position) {
        super(instance, position);
    }

    @Override
    public final void damage(@NotNull DamageSource source, double amount) {
        double oldHealth = getHealth();

        super.damage(source, amount);

        double newHealth = getHealth();

        if (newHealth != 0) {
            onDamage(source, amount);
        }

        if (newHealth != oldHealth && newHealth == 0.0) {
            onDeath(source);
            remove();
        }
    }

    /**
     * Invoked when this NonPlayerCharacter is healed.
     */
    protected void onHeal(DamageSource source, double amount) {
    }

    /**
     * Invoked when this NonPlayerCharacter takes damage but does not die as a
     * result.
     */
    protected void onDamage(DamageSource source, double amount) {
    }

    /**
     * Invoked when this NonPlayerCharacter takes damage and dies as a result.
     */
    protected void onDeath(DamageSource killer) {
    }
}
