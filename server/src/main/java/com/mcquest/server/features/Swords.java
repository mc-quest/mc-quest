package com.mcquest.server.features;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.Character;
import com.mcquest.server.character.CharacterHitbox;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.constants.Items;
import com.mcquest.server.event.PlayerCharacterBasicAttackEvent;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.instance.Instance;
import com.mcquest.server.item.Weapon;
import com.mcquest.server.physics.Collider;
import com.mcquest.server.physics.PhysicsManager;
import com.mcquest.server.physics.RaycastHit;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.sound.SoundEvent;

import java.util.List;

public class Swords implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        Items.ADVENTURERS_SWORD.onBasicAttack().subscribe(this::basicAttack);
    }

    public void basicAttack(PlayerCharacterBasicAttackEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        Weapon weapon = event.getWeapon();
        double physicalDamage = weapon.getPhysicalDamage();

        PhysicsManager physicsManager = mmorpg.getPhysicsManager();
        Instance instance = pc.getInstance();
        Pos origin = pc.getEyePosition();
        Vec direction = pc.getLookDirection();
        double maxDistance = 5.0;
        List<RaycastHit> hits = physicsManager.raycastAll(instance, origin,
                direction, maxDistance, c -> shouldDamage(pc, c));
        for (RaycastHit hit : hits) {
            CharacterHitbox hitbox = (CharacterHitbox) hit.getCollider();
            Character character = hitbox.getCharacter();
            character.damage(pc, physicalDamage);
            instance.playSound(Sound.sound(SoundEvent.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, Sound.Source.MASTER, 1f, 1f));
        }
    }

    private boolean shouldDamage(PlayerCharacter pc, Collider collider) {
        if (!(collider instanceof CharacterHitbox hitbox)) {
            return false;
        }
        Character character = hitbox.getCharacter();
        return !character.isFriendly(pc);
    }
}
