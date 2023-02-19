package com.mcquest.server.features;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.Character;
import com.mcquest.server.character.CharacterHitbox;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.constants.FighterSkills;
import com.mcquest.server.event.ActiveSkillUseEvent;
import com.mcquest.server.event.PlayerCharacterLoginEvent;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.instance.Instance;
import com.mcquest.server.physics.Collider;
import com.mcquest.server.physics.PhysicsManager;
import com.mcquest.server.util.ParticleEffects;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.particle.Particle;
import net.minestom.server.sound.SoundEvent;

public class FighterPlayerClass implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        eventHandler.addListener(PlayerCharacterLoginEvent.class, this::handleLevelUp);
        FighterSkills.BASH.onUse().subscribe(this::useBash);
        FighterSkills.SELF_HEAL.onUse().subscribe(this::useSelfHeal);
    }

    private void handleLevelUp(PlayerCharacterLoginEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        pc.setMaxMana(100);
        pc.setMana(100);
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
            if (character.isFriendly(pc)) {
                return;
            }
            hitOccurred[0] = true;
            double damageAmount = 5.0;
            character.damage(pc, damageAmount);
            Sound hitSound = Sound.sound(SoundEvent.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, Sound.Source.PLAYER, 1f, 1f);
            instance.playSound(hitSound, other.getCenter());
            ParticleEffects.particle(instance, hitboxCenter, Particle.CLOUD);
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
