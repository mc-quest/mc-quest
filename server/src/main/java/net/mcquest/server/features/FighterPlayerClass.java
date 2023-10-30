package net.mcquest.server.features;

import net.kyori.adventure.sound.Sound;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.ActiveSkillUseEvent;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.particle.ParticleEffects;
import net.mcquest.core.physics.Collider;
import net.mcquest.core.physics.Triggers;
import net.mcquest.server.constants.FighterSkills;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.particle.Particle;
import net.minestom.server.sound.SoundEvent;

import java.util.Collection;

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
        Pos hitboxCenter = pc.getEyePosition().add(pc.getLookDirection().mul(1.75));
        Vec hitboxSize = new Vec(3.5, 3.5, 3.5);

        Collection<Collider> hits = mmorpg.getPhysicsManager()
                .overlapBox(instance, hitboxCenter, hitboxSize);

        hits.forEach(Triggers.character(character -> {
            if (!character.isDamageable(pc)) {
                return;
            }
            double damageAmount = 5.0;
            character.damage(pc, damageAmount);
            Sound hitSound = Sound.sound(SoundEvent.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, Sound.Source.PLAYER, 1f, 1f);
            instance.playSound(hitSound, character.getPosition());
        }));

        ParticleEffects.particle(instance, hitboxCenter, Particle.EXPLOSION);

        if (hits.isEmpty()) {
            Sound missSound = Sound.sound(SoundEvent.ENTITY_WITHER_SHOOT, Sound.Source.PLAYER, 1f, 1.5f);
            instance.playSound(missSound, hitboxCenter);
        }
    }

    private void useSelfHeal(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        pc.heal(pc, 10.0);
    }
}
