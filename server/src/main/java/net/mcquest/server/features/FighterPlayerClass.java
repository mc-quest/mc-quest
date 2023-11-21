package net.mcquest.server.features;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.Attitude;
import net.mcquest.core.character.NonPlayerCharacter;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.ActiveSkillUseEvent;
import net.mcquest.core.event.PlayerCharacterMoveEvent;
import net.mcquest.core.event.SkillUnlockEvent;
import net.mcquest.core.event.Subscription;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.particle.ParticleEffects;
import net.mcquest.core.physics.Collider;
import net.mcquest.core.physics.Triggers;
import net.mcquest.server.constants.FighterSkills;
import net.mcquest.server.constants.PlayerClasses;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.block.Block;
import net.minestom.server.particle.Particle;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;
import java.util.Collection;

import static com.extollit.gaming.ai.path.model.Element.fire;

public class FighterPlayerClass implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        FighterSkills.BASH.onUse().subscribe(this::useBash);
        FighterSkills.SELF_HEAL.onUse().subscribe(this::useSelfHeal);
        FighterSkills.OVERHEAD_STRIKE.onUse().subscribe(this::useOverheadStrike);
        FighterSkills.TAUNT.onUse().subscribe(this::useTaunt);
        FighterSkills.BERSERK.onUse().subscribe(this::useBerserk);
        FighterSkills.WHIRLWIND.onUse().subscribe(this::useWhirlwind);
        FighterSkills.CHARGE.onUse().subscribe(this::useCharge);
    }

    private void useBash(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();

        int damageAmount = pc.getSkillManager().isUnlocked(
                FighterSkills.STRONG_ARMED)
                ? 10
                : 5;

        Instance instance = pc.getInstance();
        Pos hitboxCenter = pc.getEyePosition().add(pc.getLookDirection().mul(1.75));
        Vec hitboxSize = new Vec(3.5, 3.5, 3.5);

        Collection<Collider> hits = mmorpg.getPhysicsManager()
                .overlapBox(instance, hitboxCenter, hitboxSize);

        hits.forEach(Triggers.character(character -> {
            if (!character.isDamageable(pc)) {
                return;
            }
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
        int healAmount = pc.getSkillManager().isUnlocked(
                FighterSkills.STERNER_STUFF)
                ? 20
                : 10;


        pc.playSound(Sound.sound(SoundEvent.BLOCK_FIRE_EXTINGUISH, Sound.Source.PLAYER, 1f, 1f));
        pc.heal(pc, healAmount);
    }

    private void useOverheadStrike(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        Vec direction = new Vec(pc.getLookDirection().x(), 1, pc.getLookDirection().z());


        // Launch player into the air with impulse. The arc should move them 5 blocks.
        pc.applyImpulse(direction.mul(75 * 20));

        // Set up an event for when the player hits the ground.
        Subscription<PlayerCharacterMoveEvent>[] subscriptions = new Subscription[1];
        subscriptions[0] = pc.onMove().subscribe((pcMoveEvent) -> {
            if (!pcMoveEvent.isOnGround()
                    || pcMoveEvent.getOldPosition().y() <= pcMoveEvent.getNewPosition().y()) {
                // Return if player is in air or going up.
                return;
            }

            Instance instance = pc.getInstance();
            Pos hitboxCenter = pc.getPosition();
            Vec hitboxSize = pcMoveEvent.getPlayerCharacter().getSkillManager().isUnlocked(
                    FighterSkills.ENLARGED_OVERHEAD_STRIKE)
               ? new Vec(5, 1, 5)
               : new Vec(3, 1, 3);


            Collection<Collider> hits = mmorpg.getPhysicsManager()
                    .overlapBox(instance, hitboxCenter, hitboxSize);

            double damageAmount = pcMoveEvent.getPlayerCharacter().getSkillManager().isUnlocked(
                    FighterSkills.EMPOWERED_OVERHEAD_STRIKE)
                ?  6
                :  3;


            // Hits every character in a 5x5 range.
            hits.forEach(Triggers.character(character -> {
                if (!character.isDamageable(pc)) {
                    return;
                }
                character.damage(pc, damageAmount);
                Sound hitSound = Sound.sound(
                        SoundEvent.ENTITY_ZOMBIE_ATTACK_IRON_DOOR,
                        Sound.Source.PLAYER,
                        1f,
                        1f
                );
                instance.playSound(hitSound, character.getPosition());
                Vec launchVector = character.getPosition().sub(hitboxCenter).asVec().normalize().withY(150);
                character.applyImpulse(launchVector);
            }));

            Sound hitSound = Sound.sound(SoundEvent.ENTITY_GENERIC_EXPLODE, Sound.Source.PLAYER, 1f, 1f);
            instance.playSound(hitSound, pc.getPosition());

            // Remove listener and apply effects
            for (int i = (int) -(hitboxSize.x() / 2); i < (hitboxSize.x() / 2); i++) {
                for (int j = (int) -(hitboxSize.z() / 2); j < (hitboxSize.z() / 2); j++) {
                    ParticleEffects.particle(instance, hitboxCenter.add(i * 2, .5, j * 2), Particle.EXPLOSION);
                }
            }

            subscriptions[0].unsubscribe();
        });
    }

    private void useTaunt(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        Instance instance = pc.getInstance();
        Pos hitboxCenter = pc.getPosition();
        Vec hitboxSize = new Vec(20, 1, 20);

        Collection<Collider> hits = mmorpg.getPhysicsManager()
                .overlapBox(instance, hitboxCenter, hitboxSize);

        // Hits every entity in a 20x1x20 range and sets target to player.
        hits.forEach(Triggers.character(character -> {
            if ((character instanceof NonPlayerCharacter npc) && (character.getAttitude(pc) == Attitude.HOSTILE)) {
                npc.setTarget(pc);
            }
        }));
    }

    private void useBerserk(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();

        // Doubles health of player
        pc.setMaxHealth(pc.getMaxHealth() * 2);
        pc.heal(pc, pc.getMaxHealth() * 2);
        pc.sendMessage(Component.text("Beginning Berserk!"));

        // After 20 seconds, the current health of the player is reduced by the amount originally gained.
        // If health would be set below 1, their health is just 1
        mmorpg.getSchedulerManager().buildTask(() -> {
            double damageAmount = pc.getMaxHealth() / 2;
            if (pc.getHealth() > damageAmount) {
                pc.damage(pc, damageAmount);
            } else {
                pc.setHealth(1);
            }
            pc.sendMessage(Component.text("Berserk Ended... you are so tired"));
            pc.setMaxHealth(damageAmount);
        }).delay(Duration.ofSeconds(20)).schedule();
    }

    private void useWhirlwind(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();

        int iterations = pc.getSkillManager().isUnlocked(
                FighterSkills.AGILE_WHIRLWIND)
            ? 16
            : 8;

        Vec direction = pc.getLookDirection();

        // Create a for loop for 8 iterations
        for (int i = 1; i <= iterations; i++) {
            boolean sound = i % 2 == 1;
            Vec horizontalDir = direction.rotateAroundY(i * Math.PI / 4);
            Vec verticalDirX = direction.rotateAroundX(i * Math.PI / 4);
            Vec verticalDirZ = direction.rotateAroundZ(i * Math.PI / 4);
            mmorpg.getSchedulerManager().buildTask(() -> {
                if (sound) {
                    pc.emitSound(Sound.sound(SoundEvent.ENTITY_WITHER_SHOOT, Sound.Source.PLAYER, 1f, 1f));
                }

                Instance instance = pc.getInstance();
                Pos hitboxCenter = pc.getPosition().withY(y -> y + 1.5);
                Vec horizontalHitboxSize = new Vec(5, 2, 5);

                Collection<Collider> hits = mmorpg.getPhysicsManager()
                        .overlapBox(instance, hitboxCenter, horizontalHitboxSize);

                //Hits every entity in a 2x2 range
                hits.forEach(Triggers.character(character -> {
                    if (!character.isDamageable(pc)) {
                        return;
                    }
                    double damageAmount = 1.5;
                    character.damage(pc, damageAmount);
                }));

                pc.setLookDirection(horizontalDir);

                ParticleEffects.particle(
                        instance,
                        pc.getPosition().add(0, 1.5, 0).add(pc.getLookDirection().withY(0).normalize().mul(3.0)),
                        Particle.EXPLOSION
                );

                if (pc.getSkillManager().isUnlocked(
                        FighterSkills.MULTI_SLASH)) {

                    Vec verticalHitboxSize = new Vec(2, 5, 2);

                    hits = mmorpg.getPhysicsManager()
                            .overlapBox(instance, hitboxCenter, verticalHitboxSize);

                    hits.forEach(Triggers.character(character -> {
                        if (!character.isDamageable(pc)) {
                            return;
                        }
                        double damageAmount = 1.5;
                        character.damage(pc, damageAmount);
                    }));

                    ParticleEffects.particle(
                            instance,
                            pc.getPosition().add(1.5, 1.5, 0).add(verticalDirX.withX(0).normalize().mul(5.0)),
                            Particle.EXPLOSION);
                    ParticleEffects.particle(
                            instance,
                            pc.getPosition().add(0, 1.5, 1.5).add(verticalDirZ.withZ(0).normalize().mul(5.0)),
                            Particle.EXPLOSION);
                }

            }).delay(Duration.ofMillis((400 / iterations) * i)).schedule();
        }
    }

    private void useCharge(ActiveSkillUseEvent event) {
        double damageAmount;
        double speed;

        PlayerCharacter pc = event.getPlayerCharacter();

        if (pc.getSkillManager().isUnlocked(
                FighterSkills.DEVASTATING_CHARGE)) {
            damageAmount = 4.0;
            speed = 40.0;
        } else {
            damageAmount = 2.0;
            speed = 20.0;
        }

        // Charges player forward.
        Vec direction = pc.getLookDirection().withY(0);
        pc.setVelocity(direction.mul(speed).withY(0.0));

        pc.emitSound(Sound.sound(SoundEvent.ENTITY_WITHER_SHOOT, Sound.Source.PLAYER, 1f, 1f));

        Instance instance = pc.getInstance();

        Pos hitboxCenter = pc.getPosition().add(pc.getLookDirection().withY(0.0).mul(3.0)).withY(y -> y + 1.0);

        Vec hitboxSize = new Vec(3.0, 3.0, 3.0);

        Collection<Collider> hits = mmorpg.getPhysicsManager()
                .overlapBox(instance, hitboxCenter, hitboxSize);

        hits.forEach(Triggers.character(character -> {
            if (!character.isDamageable(pc)) {
                return;
            }
            character.damage(pc, damageAmount);
            character.applyImpulse(direction.mul(200));
        }));

        if (pc.getSkillManager().isUnlocked(
                FighterSkills.HELLISH_CHARGE)) {
            for (int i = 0; i < 3; i++) {
                Pos position = pc.getPosition().add(pc.getLookDirection().withY(0.0).mul(i));
                if (instance.getBlock(position) == Block.AIR) {
                    instance.setBlock(position, Block.FIRE);
                    mmorpg.getSchedulerManager().buildTask(() -> {
                        instance.setBlock(position, Block.AIR);
                    }).delay(Duration.ofSeconds(5)).schedule();
                }
            }
        }
    }
}