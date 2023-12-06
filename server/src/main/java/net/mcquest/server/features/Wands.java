package net.mcquest.server.features;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.AutoAttackEvent;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.particle.ParticleEffects;
import net.mcquest.core.physics.RaycastHit;
import net.mcquest.core.physics.Triggers;
import net.mcquest.server.constants.Items;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.particle.Particle;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.utils.time.Cooldown;
import net.minestom.server.utils.time.TimeUnit;

import java.time.Duration;

public class Wands implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        Items.ADVENTURERS_WAND.onAutoAttack().subscribe(this::autoAttack);
    }

    private void autoAttack(AutoAttackEvent event) {
        double maxDistance = 15.0;
        double impulse = 20.0;

        PlayerCharacter pc = event.getPlayerCharacter();
        Instance instance = pc.getInstance();
        Pos origin = pc.getEyePosition();
        Vec direction = pc.getLookDirection();
        RaycastHit hit = mmorpg.getPhysicsManager().raycast(
                instance,
                origin,
                direction,
                maxDistance,
                Triggers.raycastFilter(character -> character.isDamageable(pc))
        );

        if (hit == null) {
            ParticleEffects.line(
                    instance,
                    pc.getWeaponPosition(),
                    origin.add(direction.mul(maxDistance)),
                    Particle.CRIT,
                    2.0
            );
        } else {
            Triggers.character((character, hitPosition) -> {
                if (!character.isDamageable(pc)) {
                    return;
                }

                character.damage(pc, event.getWeapon().getPhysicalDamage());
                character.applyImpulse(direction.mul(impulse));

                instance.playSound(Sound.sound(
                        SoundEvent.ENTITY_GUARDIAN_ATTACK,
                        Sound.Source.PLAYER,
                        1f,
                        1f
                ), hitPosition);

                ParticleEffects.line(
                        instance,
                        pc.getWeaponPosition(),
                        hitPosition,
                        Particle.CRIT,
                        2.0
                );
            }).accept(hit);
        }
    }
}
