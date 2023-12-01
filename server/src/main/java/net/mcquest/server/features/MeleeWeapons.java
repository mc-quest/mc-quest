package net.mcquest.server.features;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.AutoAttackEvent;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.physics.RaycastHit;
import net.mcquest.core.physics.Triggers;
import net.mcquest.server.constants.Items;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.sound.SoundEvent;

import java.util.Collection;

public class MeleeWeapons implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        Items.ADVENTURERS_SWORD.onAutoAttack().subscribe(this::autoAttack);
        Items.IRON_DAGGER.onAutoAttack().subscribe(this::autoAttack);
    }

    public void autoAttack(AutoAttackEvent event) {
        double maxDistance = 5.0;
        double impulse = 100.0;

        PlayerCharacter pc = event.getPlayerCharacter();
        Instance instance = pc.getInstance();
        Pos origin = pc.getEyePosition();
        Vec direction = pc.getLookDirection();
        Collection<RaycastHit> hits = mmorpg.getPhysicsManager()
                .raycastAll(instance, origin, direction, maxDistance);

        hits.forEach(Triggers.character((character, hitPosition) -> {
            if (!character.isDamageable(pc)) {
                return;
            }

            character.damage(pc, event.getWeapon().getPhysicalDamage());
            character.applyImpulse(direction.mul(impulse));

            instance.playSound(Sound.sound(
                    SoundEvent.ENTITY_ZOMBIE_ATTACK_IRON_DOOR,
                    Sound.Source.PLAYER,
                    0.5f,
                    1f
            ), hitPosition);
        }));
    }
}
