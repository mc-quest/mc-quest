package net.mcquest.server.features;

import net.kyori.adventure.text.Component;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.Attitude;
import net.mcquest.core.character.NonPlayerCharacter;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.ActiveSkillUseEvent;
import net.mcquest.core.event.EventEmitter;
import net.mcquest.core.event.PlayerCharacterMoveEvent;
import net.mcquest.core.event.Subscription;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.particle.ParticleEffects;
import net.mcquest.core.physics.Collider;
import net.mcquest.core.physics.Triggers;
import net.mcquest.server.constants.FighterSkills;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.particle.Particle;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;
import java.util.Collection;

public class FighterPlayerClass implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        FighterSkills.BASH.onUse().subscribe(this::useBash);
        FighterSkills.SELF_HEAL.onUse().subscribe(this::useSelfHeal);
        FighterSkills.TREMOR.onUse().subscribe(this::useTremor);
        FighterSkills.TAUNT.onUse().subscribe(this::useTaunt);
        FighterSkills.BERSERK.onUse().subscribe(this::useBerserk);
        FighterSkills.WHIRLWIND.onUse().subscribe(this::useWhirlwind);
        FighterSkills.CHARGE.onUse().subscribe(this::useCharge);
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

    private void useTremor(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        Vec direction = new Vec(pc.getLookDirection().x(), 1, pc.getLookDirection().z());

        //Launch player into the air with impulse. The arc should move them 5 blocks
        pc.applyImpulse(direction.mul(75 * 20));

        //Set up an event for when the player hits the ground
        EventEmitter<PlayerCharacterMoveEvent> emitter = pc.onMove();
        Subscription<PlayerCharacterMoveEvent>[] subscriptions = new Subscription[1];
        subscriptions[0] = emitter.subscribe((pcMoveEvent) -> {

            //If the player hits the ground
            if(!pcMoveEvent.isOnGround()
                    || pcMoveEvent.getOldPosition().y() <= pcMoveEvent.getNewPosition().y()) {

                Instance instance = pc.getInstance();
                Pos hitboxCenter = pc.getPosition();
                Vec hitboxSize = new Vec(5, 1, 5);

                Collection<Collider> hits = mmorpg.getPhysicsManager()
                        .overlapBox(instance, hitboxCenter, hitboxSize);

                //Hits every entity in a 5x5 range
                hits.forEach(Triggers.character(character -> {
                    if (!character.isDamageable(pc)) {
                        return;
                    }
                    double damageAmount = 2.0;
                    character.damage(pc, damageAmount);
                    Sound hitSound = Sound.sound(SoundEvent.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, Sound.Source.PLAYER, 1f, 1f);
                    instance.playSound(hitSound, character.getPosition());
                    Pos charPos = character.getPosition();
                    Vec launchVector = new Vec((hitboxCenter.x() - charPos.x()) * 1000, 150, (hitboxCenter.y() - charPos.y()) * 1000);
                    character.applyImpulse(launchVector);
                }));

                // Remove listener and apply effects
                ParticleEffects.particle(instance, hitboxCenter, Particle.EXPLOSION);
                subscriptions[0].unsubscribe();
            }
        });
    }

    private void useTaunt(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        Instance instance = pc.getInstance();
        Pos hitboxCenter = pc.getPosition();
        Vec hitboxSize = new Vec(20, 1, 20);

        Collection<Collider> hits = mmorpg.getPhysicsManager()
                .overlapBox(instance, hitboxCenter, hitboxSize);

        //Hits every entity in a 20x1x20 range and sets target to player
        hits.forEach(Triggers.character(character -> {
            if ((character instanceof NonPlayerCharacter npc) && (character.getAttitude(pc) == Attitude.HOSTILE)) {
                npc = (NonPlayerCharacter) character;
                npc.setTarget(pc);
             }
        }));
    }

    private void useBerserk(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        pc.setMaxHealth(pc.getMaxHealth() * 2);
        pc.heal(pc, pc.getMaxHealth() * 2);
        pc.sendMessage(Component.text("Beginning Berserk!"));
        mmorpg.getSchedulerManager().buildTask(() -> {
            double damageAmount = pc.getMaxHealth()/2;
            if(pc.getHealth() > damageAmount) {
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

        Vec direction = pc.getLookDirection();
        Pos playerPos = pc.getPosition();
        int[] tick = new int[1];

        int iterations = 8;
        // Create a for loop for 8 iterations
        for(int i = 0; i < iterations; i++) {
            tick[0] = 0;
            // Each iteration, create a gt scheduleManager of i * .25 seconds
            // Every for loop tick hurt everything in front of where the character is looking
            mmorpg.getSchedulerManager().buildTask(() -> {

                Instance instance = pc.getInstance();
                Pos hitboxCenter = pc.getPosition();
                Vec hitboxSize = new Vec(4, 4, 4);

                Collection<Collider> hits = mmorpg.getPhysicsManager()
                        .overlapBox(instance, hitboxCenter, hitboxSize);

                //Hits every entity in a 2x2 range
                hits.forEach(Triggers.character(character -> {
                    if (!character.isDamageable(pc)) {
                        return;
                    }
                    double damageAmount = 2.0;
                    character.damage(pc, damageAmount);

                }));
                tick[0]++;

                // The new position the player is moving to
                Pos newPosition = new Pos(
                        direction.rotateAroundY(((2*Math.PI)/iterations)*tick[0]).x() * 4 + playerPos.x(),
                        playerPos.y()+1.6,
                        direction.rotateAroundY(((2*Math.PI)/iterations)*tick[0]).z() * 4 + playerPos.z());

                pc.lookAt(newPosition);

                ParticleEffects.particle(instance, newPosition, Particle.EXPLOSION);

            }).delay(Duration.ofMillis((400/iterations) * i)).schedule();
        }
    }

    private void useCharge(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        Vec direction = pc.getLookDirection();
        Vec impulse = direction.mul(75 * 10, 0, 75 * 10);
        pc.applyImpulse(impulse);

        Instance instance = pc.getInstance();
        Pos hitboxCenter = pc.getPosition();
        Vec hitboxSize = new Vec(direction.x() * 2,
                                 pc.getPosition().y()+1.6,
                                 direction.z() * 2 + pc.getPosition().z());

        Collection<Collider> hits = mmorpg.getPhysicsManager()
                .overlapBox(instance, hitboxCenter, hitboxSize);

        //Hits every entity in the direction looked at for 4 blocks
        hits.forEach(Triggers.character(character -> {
            if (!character.isDamageable(pc)) {
                return;
            }
            double damageAmount = 2.0;
            character.damage(pc, damageAmount);
            character.applyImpulse(impulse);
        }));
    }
}