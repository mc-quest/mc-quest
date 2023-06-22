package com.mcquest.server.features;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.Attitude;
import com.mcquest.core.character.Character;
import com.mcquest.core.character.CharacterHitbox;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.server.constants.FighterSkills;
import com.mcquest.core.event.ActiveSkillUseEvent;
import com.mcquest.core.feature.Feature;
import com.mcquest.core.instance.Instance;
import com.mcquest.core.physics.Collider;
import com.mcquest.core.physics.PhysicsManager;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.sound.SoundEvent;

public class FighterPlayerClass implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        FighterSkills.BASH.onUse().subscribe(this::useBash);
        FighterSkills.SELF_HEAL.onUse().subscribe(this::useSelfHeal);
    }

    private void useBash(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        Instance instance = pc.getInstance();
        Pos hitboxCenter = pc.getEyePosition().add(pc.getLookDirection().mul(1.5));
        Vec hitboxSize = new Vec(1.5, 1.5, 1.5);
        Collider hitbox = new Collider(instance, hitboxCenter, hitboxSize);
        boolean[] hitOccurred = {false};

        hitbox.onCollisionEnter(other -> {
            if (!(other instanceof CharacterHitbox characterHitbox)) {
                return;
            }
            Character character = characterHitbox.getCharacter();
            if (character.getAttitude(pc) == Attitude.FRIENDLY) {
                return;
            }
            hitOccurred[0] = true;
            double damageAmount = 5.0;
            character.damage(pc, damageAmount);
            Sound hitSound = Sound.sound(SoundEvent.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, Sound.Source.PLAYER, 1f, 1f);
            instance.playSound(hitSound, other.getCenter());
        });
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();
        physicsManager.addCollider(hitbox);
        physicsManager.removeCollider(hitbox);
        if (!hitOccurred[0]) {
            Sound missSound = Sound.sound(SoundEvent.ENTITY_WITHER_SHOOT, Sound.Source.PLAYER, 1f, 1.5f);
            instance.playSound(missSound);
        }
    }

    private void useSelfHeal(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        pc.heal(pc, 10.0);
    }
}
