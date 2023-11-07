package net.mcquest.server.features;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.ActiveSkillUseEvent;
import net.mcquest.core.event.SkillUnlockEvent;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.particle.ParticleEffects;
import net.mcquest.core.physics.Collider;
import net.mcquest.core.physics.Triggers;
import net.mcquest.server.constants.RogueSkills;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.particle.Particle;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;
import java.util.Collection;

public class RoguePlayerClass implements Feature {

    private Mmorpg mmorpg;


    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;

        // Defines what happens when the "Dash" ability is used
        RogueSkills.DASH.onUse().subscribe(this::useDash);
        RogueSkills.BACKSTAB.onUse().subscribe(this::useBackstab);
        RogueSkills.SNEAK.onUse().subscribe(this::useSneak);
        // RogueSkills.ADRENALINE.onUse().subscribe(this::useAdrenaline);
        RogueSkills.FANOFKNIVES.onUse().subscribe(this::useFanOfKnives);
        // RogueSkills.FLEETOFFOOT.onUnlock().subscribe(this::useFleetofFoot);
    }

    private void useFanOfKnives(ActiveSkillUseEvent event) {
        double damageAmount = 3.0;
        double maxDistance = 20.0;
        double arrowSpeed = 20.0;
        Vec hitboxSize = new Vec(1f, 1f, 1f);

        PlayerCharacter pc = event.getPlayerCharacter();
        Instance instance = pc.getInstance();
        Pos startPosition = pc.getWeaponPosition().add(pc.getLookDirection().mul(1f));

        Vec centerKnife = pc.getLookDirection();
        Vec leftKnife = pc.getLookDirection().rotateAroundY(Math.PI/4);
        Vec rightKnife = pc.getLookDirection().rotateAroundY(-Math.PI/4);
        Vec[] velocities = { leftKnife, centerKnife, rightKnife};

        for(int i = 0; i < 3; i++) {
            Collider hitbox = new Collider(instance, startPosition, hitboxSize);
            Vec arrowVelocity = velocities[i].mul(arrowSpeed);
            Entity arrowEntity = new Entity(EntityType.ARROW) {
                @Override
                public void tick(long time) {
                    super.tick(time);
                    hitbox.setCenter(this.getPosition().add(0, 0.5, 0));
                    setVelocity(arrowVelocity);
                    if (getPosition().distanceSquared(startPosition) > maxDistance * maxDistance) {
                        remove();
                        hitbox.remove();
                    }
                }
            };

            hitbox.onCollisionEnter(Triggers.character(character -> {
                if (!character.isDamageable(pc)) {
                    return;
                }

                character.damage(pc, damageAmount);
                arrowEntity.remove();
                hitbox.remove();
                ParticleEffects.particle(instance, hitbox.getCenter(), Particle.EXPLOSION);

                instance.playSound(Sound.sound(
                        SoundEvent.ENTITY_DRAGON_FIREBALL_EXPLODE,
                        Sound.Source.PLAYER,
                        1f,
                        1f
                ), character.getPosition());
            }));

            arrowEntity.setNoGravity(true);
            arrowEntity.setInstance(instance, startPosition);

            mmorpg.getPhysicsManager().addCollider(hitbox);
        }
    }

    private void useDash(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        Vec direction = pc.getLookDirection();
        Instance instance = pc.getInstance();
        Pos hitboxCenter = pc.getEyePosition().add(pc.getLookDirection().mul(1.75));

        // Makes particle effects
        ParticleEffects.particle(instance, hitboxCenter, Particle.SMOKE);
        ParticleEffects.particle(instance, hitboxCenter, Particle.FLASH);

        // Launches player in direction of vector
        pc.applyImpulse(direction.mul(75 * 30, 0, 75 * 30));
    }


    private void useBackstab(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();

        Instance instance = pc.getInstance();
        Pos hitboxCenter = pc.getEyePosition().add(pc.getLookDirection().mul(1.75));
        Vec hitboxSize = new Vec(1, 1, 0);

        //Gets collection of hit characters by hitbox
        Collection<Collider> hits = mmorpg.getPhysicsManager()
                .overlapBox(instance, hitboxCenter, hitboxSize);

        //For each character hit
        hits.forEach(Triggers.character(character -> {

            // If the character is in the line of sight of the creature being hit or it isn't damageable then do nothing
           if (!character.isDamageable(pc) || character.hasLineOfSight(pc, false)) {
               return;
           }

            //Calculate the damage of the item being held in the hand and increase it by 50%
            double damageAmount =  pc.getInventory().getWeapon().getPhysicalDamage() * 2;
            //Apply damage to creature hit within hitbox
            ParticleEffects.particle(instance, hitboxCenter, Particle.SMOKE);
            character.damage(pc, damageAmount);
            Sound hitSound = Sound.sound(SoundEvent.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, Sound.Source.PLAYER, 1f, 1f);
            instance.playSound(hitSound, character.getPosition());

        }));

        //If nothing was hit make sound signifying they haven't
        if (hits.isEmpty()) {
            Sound missSound = Sound.sound(SoundEvent.ENTITY_WITHER_SHOOT, Sound.Source.PLAYER, 1f, 1.5f);
            instance.playSound(missSound, hitboxCenter);
        }
    }

    private void useSneak(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();

        pc.setInvisible(true);
        mmorpg.getSchedulerManager().buildTask(() -> {
            pc.setInvisible(false);
        }).delay(Duration.ofSeconds(5)).schedule();
    }

//    private void useAdrenaline(ActiveSkillUseEvent event) {
//        PlayerCharacter pc = event.getPlayerCharacter();
//        pc.updateAttackSpeed(20);
//
//        mmorpg.getSchedulerManager().buildTask(() -> {
//            pc.updateAttackSpeed(pc.getInventory().getWeapon().getAttackSpeed());
//        }).delay(Duration.ofSeconds(5)).schedule();
//    }
//
//    private void useFleetofFoot(SkillUnlockEvent event) {
//        PlayerCharacter pc = event.getPlayerCharacter();
//        pc.changeSpeed(.1f);
//    }
}
