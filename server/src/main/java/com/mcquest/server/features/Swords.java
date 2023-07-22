package com.mcquest.server.features;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.Attitude;
import com.mcquest.core.character.Character;
import com.mcquest.core.character.CharacterHitbox;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.server.constants.Items;
import com.mcquest.core.event.AutoAttackEvent;
import com.mcquest.core.feature.Feature;
import com.mcquest.core.instance.Instance;
import com.mcquest.core.item.Weapon;
import com.mcquest.core.physics.Collider;
import com.mcquest.core.physics.PhysicsManager;
import com.mcquest.core.physics.RaycastHit;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.sound.SoundEvent;

import java.util.Collection;
import java.util.List;

public class Swords implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        Items.ADVENTURERS_SWORD.onAutoAttack().subscribe(this::basicAttack);
    }

    public void basicAttack(AutoAttackEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        Weapon weapon = event.getWeapon();
        double physicalDamage = weapon.getPhysicalDamage();

        PhysicsManager physicsManager = mmorpg.getPhysicsManager();
        Instance instance = pc.getInstance();
        Pos origin = pc.getEyePosition();
        Vec direction = pc.getLookDirection();
        double maxDistance = 5.0;
        Collection<RaycastHit> hits = physicsManager.raycastAll(instance, origin,
                direction, maxDistance, c -> shouldDamage(pc, c));
        for (RaycastHit hit : hits) {
            CharacterHitbox hitbox = (CharacterHitbox) hit.getCollider();
            Character character = hitbox.getCharacter();
            character.damage(pc, physicalDamage);
            instance.playSound(Sound.sound(SoundEvent.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, Sound.Source.MASTER, 0.5f, 1f));
        }
    }

    private boolean shouldDamage(PlayerCharacter pc, Collider collider) {
        if (!(collider instanceof CharacterHitbox hitbox)) {
            return false;
        }
        Character character = hitbox.getCharacter();
        return character.isDamageable(pc);
    }
}
