package com.mcquest.server.features;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.Character;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.event.AutoAttackEvent;
import com.mcquest.core.feature.Feature;
import com.mcquest.core.instance.Instance;
import com.mcquest.core.physics.RaycastHit;
import com.mcquest.core.physics.Triggers;
import com.mcquest.server.constants.Items;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.sound.SoundEvent;

import java.util.Collection;
import java.util.function.BiConsumer;

public class Swords implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        Items.ADVENTURERS_SWORD.onAutoAttack().subscribe(this::basicAttack);
    }

    public void basicAttack(AutoAttackEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();

        Instance instance = pc.getInstance();
        Pos origin = pc.getEyePosition();
        Vec direction = pc.getLookDirection();
        double maxDistance = 5.0;
        Collection<RaycastHit> hits = mmorpg.getPhysicsManager()
                .raycastAll(instance, origin, direction, maxDistance);

        BiConsumer<Pos, Character> swordHit = (position, character) -> {
            if (!character.isDamageable(pc)) {
                return;
            }

            character.damage(pc, event.getWeapon().getPhysicalDamage());
            Sound hitSound =
                    Sound.sound(SoundEvent.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, Sound.Source.MASTER, 0.5f, 1f);
            instance.playSound(hitSound);
        };

        hits.forEach(Triggers.character(swordHit));
    }
}
