package net.mcquest.server.features;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.ActiveSkillUseEvent;
import net.mcquest.core.event.PlayerCharacterMoveEvent;
import net.mcquest.core.event.Subscription;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.particle.ParticleEffects;
import net.mcquest.core.physics.Collider;
import net.mcquest.core.physics.Triggers;
import net.mcquest.server.constants.PlayerClasses;
import net.mcquest.server.constants.RogueSkills;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.block.Block;
import net.minestom.server.particle.Particle;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class RoguePlayerClass implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;

        RogueSkills.DASH.onUse().subscribe(this::useDash);
        RogueSkills.BACKSTAB.onUse().subscribe(this::useBackstab);
        RogueSkills.SNEAK.onUse().subscribe(this::useSneak);
        RogueSkills.FAN_OF_KNIVES.onUse().subscribe(this::useFanOfKnives);
        RogueSkills.WOUNDING_SLASH.onUse().subscribe(this::useWoundingSlash);
    }

    private void useDash(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();

        int speed = pc.getSkillManager().isUnlocked(
                RogueSkills.SWIFT_OF_FOOT)
                ? 100
                : 50;

        // Makes particle effects
        poof(pc);

        // Launches player in direction of vector
        Vec direction = pc.getLookDirection();
        pc.setVelocity(direction.withY(0).mul(speed));

        if(pc.getSkillManager().isUnlocked(
                RogueSkills.LIGHT_OF_FOOT)) {
            // Set IInvisible for duration of sprint
            pc.setInvisible(true);
            Subscription<PlayerCharacterMoveEvent>[] subscriptions = new Subscription[1];
            subscriptions[0] = pc.onMove().subscribe((pcMoveEvent) -> {
                Vec playerVelocity = pcMoveEvent.getPlayerCharacter().getVelocity();
                if (playerVelocity.x() != 0 && playerVelocity.z() != 0) {
                    return;
                }
                pcMoveEvent.getPlayerCharacter().setInvisible(false);
                subscriptions[0].unsubscribe();
            });
        }

    }


    private void useBackstab(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();

        Instance instance = pc.getInstance();
        Pos hitboxCenter = pc.getEyePosition().add(pc.getLookDirection().mul(1.75));
        Vec hitboxSize = new Vec(1, 1, 1);

        Collection<Collider> hits = mmorpg.getPhysicsManager()
                .overlapBox(instance, hitboxCenter, hitboxSize);

        hits.forEach(Triggers.character(character -> {
            double damageAmount = pc.getInventory().getWeapon().getPhysicalDamage();

            if (!character.isDamageable(pc)) {
                return;
            }

            if (pc.getSkillManager().isUnlocked(
                    RogueSkills.SHADOW_STEP)) {
                // Get positon on other side of entity
                Pos position = pc.getPosition().add(pc.getLookDirection().withY(0.0).mul(2));

                // If there is air on the other side of the entity, teleport to the other side and
                // look at it
                if (instance.getBlock(position.add(0, 1, 0)) == Block.AIR
                        && instance.getBlock(position.add(0, 2, 0)) == Block.AIR) {
                    poof(pc);
                    pc.setPosition(position);
                    pc.setLookDirection(pc.getLookDirection().rotateAroundY(Math.PI));
                    poof(pc);
                }
            }

            int modifier = pc.getSkillManager().isUnlocked(
                    RogueSkills.GO_FOR_THE_JUGULAR)
                    ? 4
                    : 2;

            if (character.hasLineOfSight(pc, true)) {
                character.damage(pc, damageAmount);
            } else {
                character.damage(pc, damageAmount * modifier);
            }

            // Apply damage to creature hit within hitbox.
            ParticleEffects.particle(instance, hitboxCenter, Particle.SMOKE);
            Sound hitSound = Sound.sound(SoundEvent.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, Sound.Source.PLAYER, 1f, 1f);
            instance.playSound(hitSound, character.getPosition());
        }));

        // If nothing was hit make sound signifying they haven't.
        if (hits.isEmpty()) {
            Sound missSound = Sound.sound(SoundEvent.ENTITY_WITHER_SHOOT, Sound.Source.PLAYER, 1f, 1.5f);
            instance.playSound(missSound, hitboxCenter);
        }
    }

    private void useSneak(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        poof(pc);
        pc.setInvisible(true);

        int duration = pc.getSkillManager().isUnlocked(
                RogueSkills.ONE_WITH_SHADOWS)
                ? 10
                : 5;

        mmorpg.getSchedulerManager().buildTask(() -> {
            poof(pc);
            pc.setInvisible(false);
        }).delay(Duration.ofSeconds(duration)).schedule();
    }

    private void poof(PlayerCharacter pc) {
        Instance instance = pc.getInstance();
        Pos particlePosition = pc.getPosition().withY(y -> y + 1);
        ParticleEffects.particle(instance, particlePosition, Particle.SMOKE);
        ParticleEffects.particle(instance, particlePosition, Particle.FLASH);
        pc.emitSound(Sound.sound(SoundEvent.ENTITY_BLAZE_SHOOT, Sound.Source.PLAYER, 1f, 1f));
    }

    private void useFanOfKnives(ActiveSkillUseEvent event) {
        double damageAmount = 3.0;
        double arrowSpeed = 20.0;
        Vec hitboxSize = new Vec(1f, 1f, 1f);

        PlayerCharacter pc = event.getPlayerCharacter();
        Instance instance = pc.getInstance();
        Pos startPosition = pc.getWeaponPosition().add(pc.getLookDirection().mul(1f));

        // Number of knives = 1 + (knifeMultiplier*2)
        int knifeMultiplier = pc.getSkillManager().isUnlocked(
                RogueSkills.KNIFE_MASTER)
                ? 2
                : 1;


        for (double i = -Math.PI / 4;
             i <= Math.PI / 4;
             i += Math.PI / (4 * knifeMultiplier)) {
            Vec[] arrowVelocity = new Vec[1];
            arrowVelocity[0] = pc.getLookDirection().rotateAroundY(i).mul(arrowSpeed);

            long startTime = System.currentTimeMillis();
            Collider hitbox = new Collider(instance, startPosition, hitboxSize);
            Entity arrowEntity = new Entity(EntityType.ARROW) {
                @Override
                public void tick(long time) {
                    super.tick(time);
                    hitbox.setCenter(this.getPosition().add(0, 0.5, 0));
                    setVelocity(arrowVelocity[0]);

                    if (pc.getSkillManager().isUnlocked(
                            RogueSkills.BOUNCING_BLADES)) {

                        // Checks 10% of a block ahead to see if it will collide in the next tick with an object
                        Pos direction = getPosition().add(arrowVelocity[0].mul(0.1));
                        if (instance.getBlock(direction) != Block.AIR) {
                            arrowVelocity[0] = arrowVelocity[0].neg();
                        }
                    }

                    // Destroy the arrows after 5 seconds
                    if ((System.currentTimeMillis() - startTime) >= 5000) {
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


    private void useWoundingSlash(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();

        int damageOccurences = pc.getSkillManager().isUnlocked(
                RogueSkills.DEEP_CUT)
                ? 10
                : 5;

        int damageSpeed = pc.getSkillManager().isUnlocked(
                RogueSkills.ANTICOAGULANT_POISON)
                ? 1
                : 2;

        Instance instance = pc.getInstance();
        Pos hitboxCenter = pc.getEyePosition().add(pc.getLookDirection().mul(1.75));
        Vec hitboxSize = new Vec(1, 1, 1);

        // Gets collection of hit characters by hitbox.
        Collection<Collider> hits = mmorpg.getPhysicsManager()
                .overlapBox(instance, hitboxCenter, hitboxSize);

        // For each character hit.
        hits.forEach(Triggers.character(character -> {

            // If the character isn't damageable do nothing.
            if (!character.isDamageable(pc)) {
                return;
            }

            // Hurt the player over time (2 damage every 2 seconds for 10 seconds).
            for (int i = 0; i < damageOccurences; i++) {
                mmorpg.getSchedulerManager().buildTask(() -> {
                    if (character.isAlive()) {
                        character.damage(pc, 2);
                        Sound poisonSound = Sound.sound(SoundEvent.BLOCK_GRAVEL_FALL, Sound.Source.PLAYER, 1f, 1f);
                        instance.playSound(poisonSound, character.getPosition());
                        ParticleEffects.particle(instance, character.getPosition(), Particle.DRIPPING_LAVA);

                        if (pc.getSkillManager().isUnlocked(RogueSkills.EXPLOSIVE_POISON)) {
                            if (!character.isAlive()) {
                                Pos explosionCenter = character.getPosition();
                                Vec explosionSize = new Vec(5, 5, 5);

                                // Gets collection of hit characters by hitbox.
                                Collection<Collider> explosionHits = mmorpg.getPhysicsManager()
                                        .overlapBox(instance, explosionCenter, explosionSize);

                                // For each character hit.
                                explosionHits.forEach(Triggers.character(explosionCharacter -> {

                                    // If the character isn't damageable do nothing.
                                    if (!explosionCharacter.isDamageable(pc)) {
                                        return;
                                    }
                                    explosionCharacter.damage(pc, 5);

                                }));


                                Sound hitSound = Sound.sound(SoundEvent.ENTITY_GENERIC_EXPLODE, Sound.Source.PLAYER, 1f, 1f);
                                instance.playSound(hitSound, pc.getPosition());

                                // Make the 5x5x5 explosion
                                for (int j = (int) -(explosionSize.x() / 2); j < (explosionSize.x() / 2); j++) {
                                    for (int k = (int) -(explosionSize.z() / 2); k < (explosionSize.z() / 2); k++) {
                                        for (int l = (int) -(explosionSize.z() / 2); l < (explosionSize.z() / 2); l++) {
                                            ParticleEffects.particle(instance, explosionCenter.add(j, l, k), Particle.EXPLOSION);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }).delay(Duration.ofSeconds(damageSpeed * i)).schedule();
            }

            ParticleEffects.particle(instance, hitboxCenter, Particle.CRIT);
            Sound hitSound = Sound.sound(SoundEvent.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, Sound.Source.PLAYER, 1f, 1f);
            instance.playSound(hitSound, character.getPosition());
        }));

        // If nothing was hit make sound signifying they haven't.
        if (hits.isEmpty()) {
            Sound missSound = Sound.sound(SoundEvent.ENTITY_WITHER_SHOOT, Sound.Source.PLAYER, 1f, 1.5f);
            instance.playSound(missSound, hitboxCenter);
        }
    }
}
