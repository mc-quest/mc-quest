package com.mcquest.core.character;

import com.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.NotNull;

public class NonPlayerCharacter extends Character {
    public NonPlayerCharacter(@NotNull Instance instance, @NotNull Pos position,
                              @NotNull Vec boundingBox) {
        super(instance, position, boundingBox);
    }

    @MustBeInvokedByOverriders
    public void setLevel(int level) {
        super.setLevel(level);
    }

    @Override
    @MustBeInvokedByOverriders
    public void damage(@NotNull DamageSource source, double amount) {
        super.damage(source, amount);

        if (getHealth() == 0.0) {
            onDeath(source);
        } else {
            onDamage(source);
        }
    }

    @Override
    @MustBeInvokedByOverriders
    public void heal(@NotNull DamageSource source, double amount) {
        super.heal(source, amount);

        onHeal(source);
    }

    /**
     * Convenience method invoked when this NonPlayerCharacter takes damage but
     * does not die as a result.
     */
    protected void onDamage(DamageSource source) {
    }

    /**
     * Convenience method invoked when this NonPlayerCharacter is healed.
     */
    protected void onHeal(DamageSource source) {
    }

    /**
     * Convenience method invoked when this NonPlayerCharacter takes damage and
     * dies as a result.
     */
    protected void onDeath(DamageSource killer) {
    }
}
