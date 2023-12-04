package net.mcquest.server.features;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.Character;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.ActiveSkillUseEvent;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.particle.ParticleEffects;
import net.mcquest.core.physics.Collider;
import net.mcquest.core.physics.RaycastHit;
import net.mcquest.core.physics.Triggers;
import net.mcquest.server.constants.MageSkills;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.particle.Particle;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MagePlayerClass implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        MageSkills.FIREBALL.onUse().subscribe(this::useFireball);
        MageSkills.ICE_BEAM.onUse().subscribe(this::useIceBeam);
        MageSkills.CHAIN_LIGHTNING.onUse().subscribe(this::useChainLightning);
    }

    public void useFireball(ActiveSkillUseEvent event) {
        double damageAmount = 6.0;
        double maxDistance = 20.0;
        double fireballSpeed = 20.0;
        Vec hitboxSize = new Vec(1f, 1f, 1f);

        PlayerCharacter pc = event.getPlayerCharacter();
        Instance instance = pc.getInstance();
        Pos startPosition = pc.getWeaponPosition().add(pc.getLookDirection().mul(1f));

        Collider hitbox = new Collider(instance, startPosition, hitboxSize);

        Vec fireballVelocity = pc.getLookDirection().mul(fireballSpeed);
        Entity fireballEntity = new Entity(EntityType.FIREBALL) {
            @Override
            public void tick(long time) {
                super.tick(time);
                hitbox.setCenter(this.getPosition().add(0, 0.5, 0));
                setVelocity(fireballVelocity);
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

            fireballEntity.remove();
            hitbox.remove();

            ParticleEffects.particle(instance, hitbox.getCenter(), Particle.EXPLOSION);

            instance.playSound(Sound.sound(
                    SoundEvent.ENTITY_DRAGON_FIREBALL_EXPLODE,
                    Sound.Source.PLAYER,
                    1f,
                    1f
            ), character.getPosition());
        }));

        fireballEntity.setNoGravity(true);
        fireballEntity.setInstance(instance, startPosition);

        mmorpg.getPhysicsManager().addCollider(hitbox);

        instance.playSound(Sound.sound(
                SoundEvent.BLOCK_FIRE_EXTINGUISH,
                Sound.Source.PLAYER,
                1f,
                1f
        ), startPosition);
    }

    private void useIceBeam(ActiveSkillUseEvent event) {
        double maxDistance = 15.0;
        double damagePerTick = 1.0;
        double impulse = 10.0;
        Particle particle = Particle.ITEM_SNOWBALL;
        double particleDensity = 3.0;
        int tickCount = 12;
        long tickPeriodMs = 250;

        PlayerCharacter pc = event.getPlayerCharacter();

        for (int tick = 0; tick < tickCount; tick++) {
            mmorpg.getSchedulerManager().buildTask(() -> {
                Instance instance = pc.getInstance();
                Pos origin = pc.getWeaponPosition();
                Vec direction = pc.getEyePosition()
                        .add(pc.getLookDirection().mul(maxDistance))
                        .sub(origin)
                        .asVec()
                        .normalize();
                Pos targetBlock = pc.getTargetBlockPosition(maxDistance);
                double distance = targetBlock == null
                        ? maxDistance
                        : Math.min(maxDistance, targetBlock.distance(origin));
                RaycastHit hit = mmorpg.getPhysicsManager().raycast(
                        instance,
                        origin,
                        direction,
                        distance,
                        Triggers.raycastFilter(character -> character.isDamageable(pc))
                );
                if (hit == null) {
                    ParticleEffects.line(
                            instance,
                            origin,
                            direction,
                            distance,
                            particle,
                            particleDensity
                    );

                    instance.playSound(Sound.sound(
                            SoundEvent.BLOCK_GLASS_BREAK,
                            Sound.Source.PLAYER,
                            1f,
                            1.5f
                    ), origin);
                } else {
                    Triggers.character((character, hitPosition) -> {
                        ParticleEffects.line(
                                instance,
                                origin,
                                hitPosition,
                                particle,
                                particleDensity
                        );
                        character.damage(pc, damagePerTick);
                        character.applyImpulse(direction.mul(impulse));

                        instance.playSound(Sound.sound(
                                SoundEvent.BLOCK_GLASS_BREAK,
                                Sound.Source.PLAYER,
                                1f,
                                1f
                        ), hitPosition);
                    }).accept(hit);
                }
            }).delay(Duration.ofMillis(tickPeriodMs * tick)).schedule();
        }

        pc.setCanAct(false);
        mmorpg.getSchedulerManager().buildTask(() -> pc.setCanAct(true))
                .delay(Duration.ofMillis(tickPeriodMs * tickCount))
                .schedule();
    }

    private void useChainLightning(ActiveSkillUseEvent event) {
        int chains = 3;
        double chainRange = 10.0;

        PlayerCharacter pc = event.getPlayerCharacter();
        Instance instance = pc.getInstance();
        Pos origin = pc.getEyePosition();
        Vec direction = pc.getLookDirection();

        RaycastHit initialHit = mmorpg.getPhysicsManager().raycast(
                instance,
                origin,
                direction,
                chainRange,
                Triggers.raycastFilter(character -> character.isDamageable(pc))
        );

        if (initialHit == null) {
            ParticleEffects.line(
                    instance,
                    pc.getWeaponPosition(),
                    origin.add(direction.mul(chainRange)),
                    Particle.DRAGON_BREATH,
                    3.0
            );
            return;
        }

        Triggers.character((character, hitPosition) -> {
            if (!character.isDamageable(pc)) {
                return;
            }

            ParticleEffects.line(
                    instance,
                    pc.getWeaponPosition(),
                    hitPosition,
                    Particle.DRAGON_BREATH,
                    3.0
            );

            character.damage(pc, 10);

            Pos characterPos = character.getPosition().add(0.0, 1.0, 0.0);
            List<Character> hits = new ArrayList<>();
            hits.add(character);
            mmorpg.getSchedulerManager().buildTask(() -> {
                chainLightning(
                        event,
                        chains,
                        characterPos,
                        chainRange,
                        hits
                );
            }).delay(Duration.ofMillis(200L)).schedule();
        }).accept(initialHit);
    }

    private void chainLightning(ActiveSkillUseEvent event, int chains, Pos chainOrigin, double chainRange, List<Character> hits) {
        if (chains == 0) {
            return;
        }

        PlayerCharacter pc = event.getPlayerCharacter();
        Instance instance = pc.getInstance();
        Vec chainHitboxSize = new Vec(2 * chainRange, 2 * chainRange, 2 * chainRange);

        Collection<Collider> chainHitbox = mmorpg.getPhysicsManager()
                .overlapBox(instance, chainOrigin, chainHitboxSize);
        List<Character> targets = new ArrayList<>();
        chainHitbox.forEach(Triggers.character(character -> {
            if (!character.isDamageable(pc) || hits.contains(character)) {
                return;
            }
            targets.add(character);
        }));
        if (targets.isEmpty()) {
            return;
        }

        Character character = targets.get(0);
        double shortestDistance = chainOrigin.distance(character.getPosition());
        for (Character target : targets) {
            double distance = chainOrigin.distance(target.getPosition());
            if (distance < shortestDistance) {
                character = target;
                shortestDistance = distance;
            }
        }
        hits.add(character);

        Pos characterPos = character.getPosition().add(0.0, 1.0, 0.0);

        ParticleEffects.line(
                instance,
                chainOrigin,
                characterPos,
                Particle.DRAGON_BREATH,
                3.0
        );

        character.damage(pc, 10);

        mmorpg.getSchedulerManager().buildTask(() -> {
            chainLightning(
                    event,
                    chains - 1,
                    characterPos,
                    chainRange,
                    hits
            );
        }).delay(Duration.ofMillis(200L)).schedule();
    }
}
