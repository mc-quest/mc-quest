package net.mcquest.server.features;

import net.kyori.adventure.sound.Sound;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.ActiveSkillUseEvent;
import net.mcquest.core.event.PlayerCharacterMoveEvent;
import net.mcquest.core.event.SkillUnlockEvent;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.particle.ParticleEffects;
import net.mcquest.core.physics.Collider;
import net.mcquest.core.physics.RaycastHit;
import net.mcquest.core.physics.Triggers;
import net.mcquest.server.constants.RogueSkills;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.Player;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventListener;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerMoveEvent;
import net.minestom.server.event.player.PlayerTickEvent;
import net.minestom.server.network.packet.server.play.PlayerAbilitiesPacket;
import net.minestom.server.particle.Particle;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import net.minestom.server.sound.SoundEvent;

import java.time.Clock;
import java.time.Instant;
import java.util.Collection;

import static net.minestom.server.utils.PacketUtils.sendPacket;

public class RoguePlayerClass implements Feature {

    private Mmorpg mmorpg;


    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;

        // Defines what happens when the "Dash" ability is used
        RogueSkills.DASH.onUse().subscribe(this::useDash);
        RogueSkills.BACKSTAB.onUse().subscribe(this::useBackstab);
        RogueSkills.SNEAK.onUse().subscribe(this::useSneak);
        RogueSkills.ADRENALINE.onUse().subscribe(this::useAdrenaline);
        // RogueSkills.FLEETOFFOOT.onUnlock().subscribe(this::useFleetofFoot);
    }



    private void useDash(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        Vec direction = pc.getLookDirection();
        Instance instance = pc.getInstance();
        Pos hitboxCenter = pc.getEyePosition().add(pc.getLookDirection().mul(1.75));

        // Makes partical effects
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
//            if (!character.isDamageable(pc) || pc.creatureHasSight(character)) {
//                return;
//            }

            //Calculate the damage of the item being held in the hand and increase it by 50%
            //double damageAmount = pc.getWeapon().getPhysicalDamage() * 1.5;

            double damageAmount = 0;
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

        long startTime = System.currentTimeMillis();

        pc.setInvisible(true);
        //pc.sendMessage("Is PC invisible?: " + pc.isInvisible());
        EventListener[] listener = new EventListener[1];
        listener[0] = EventListener.of(PlayerTickEvent.class, tick -> {
            //pc.sendMessage("Still Invisible?: " + pc.isInvisible());
            long currTime = System.currentTimeMillis();
            if((startTime+5000) < currTime) {
                pc.setInvisible(false);
                //pc.sendMessage("All done being invisible! Invisiblity: " + pc.isInvisible());
                mmorpg.getGlobalEventHandler().removeListener(listener[0]);
            }
        });

        mmorpg.getGlobalEventHandler().addListener(listener[0]);
    }

    private void useAdrenaline(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();

       // pc.addEffect(PotionEffect.HASTE, 5);// pc.addEffect(PotionEffect.STRENGTH, 5);
    }

    private void useFleetofFoot(SkillUnlockEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        // pc.changeSpeed(.1f);
    }

}
