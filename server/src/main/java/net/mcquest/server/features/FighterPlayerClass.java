package net.mcquest.server.features;

import net.kyori.adventure.text.Component;
import net.mcquest.core.Mmorpg;
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
import net.mcquest.server.constants.Items;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerTickEvent;
import net.minestom.server.particle.Particle;
import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Collection;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

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
        pc.applyImpulse(direction.mul(75 * 20, 75 * 20, 75 * 20));

        //Set up an event for when the player hits the ground

        EventListener[] listener = new EventListener[1];
        listener[0] = EventListener.of(PlayerCharacterMoveEvent.class, playerMoveEvent -> {

            //playerMoveEvent.getPlayer().sendMessage("Velocty: " + playerMoveEvent.getPlayer().getVelocity().y());
            //If the player hits the ground
            if (playerMoveEvent.getPlayerCharacter().getVelocity().y() == -1.568
                    && playerMoveEvent.getPlayerCharacter().isOnGround()) {

                //playerMoveEvent.getPlayer().sendMessage("On Ground");
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
                    Vec launchVector = new Vec((hitboxCenter.x() + charPos.x()) * 1000, 150, (hitboxCenter.y() + charPos.y()) * 1000);
                    // playerMoveEvent.getPlayerCharacter().sendMessage(launchVector.toString());
                    character.applyImpulse(launchVector);
                }));

                // Remove listener and apply effects
                ParticleEffects.particle(instance, hitboxCenter, Particle.EXPLOSION);
                mmorpg.getGlobalEventHandler().removeListener(listener[0]);
            }
        });


        mmorpg.getGlobalEventHandler().addListener(listener[0]);


    }

    public void useTaunt(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        Instance instance = pc.getInstance();
        Pos hitboxCenter = pc.getPosition();
        Vec hitboxSize = new Vec(20, 1, 20);

        Collection<Collider> hits = mmorpg.getPhysicsManager()
                .overlapBox(instance, hitboxCenter, hitboxSize);

        //Hits every entity in a 20x1x20 range and sets target to player
        hits.forEach(Triggers.character(character -> {
            if (character instanceof NonPlayerCharacter) {
                NonPlayerCharacter npc = (NonPlayerCharacter) character;
                npc.setTarget(pc);
                // pc.sendMessage(npc.getName() + " is now attacking " + pc.getName());
            }
        }));
    }

    public void useBerserk(ActiveSkillUseEvent event) {

        PlayerCharacter pc = event.getPlayerCharacter();
        pc.setMaxHealth(pc.getMaxHealth() * 2);
        pc.heal(pc, pc.getMaxHealth() * 2);

        mmorpg.getSchedulerManager().buildTask(() -> {
            pc.damage(pc, pc.getMaxHealth() / 2);
            pc.setMaxHealth(pc.getMaxHealth() / 2);
        }).delay(Duration.ofSeconds(20)).schedule();

    }

    public void useWhirlwind(ActiveSkillUseEvent event) {

        PlayerCharacter pc = event.getPlayerCharacter();

        Vec direction = pc.getLookDirection();
        Pos playerPos = pc.getPosition();
        int[] tick = new int[1];

        // Create a for loop for 8 iterations
        for(int i = 0; i < 8; i++) {
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
                    Sound hitSound = Sound.sound(SoundEvent.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, Sound.Source.PLAYER, 1f, 1f);
                }));
                tick[0]++;
                pc.sendMessage(Component.text("Rotate: " + tick[0]));

                Pos newPosition = new Pos(direction.rotateAroundY((Math.PI/4)*tick[0]).x() * 5 + playerPos.x(),
                        playerPos.y()+1.6,
                        direction.rotateAroundY((Math.PI/4)*tick[0]).z() * 5 + playerPos.z());

                pc.lookAt(newPosition);

            }).delay(Duration.ofMillis(250 * i)).schedule();
        }
    }
}