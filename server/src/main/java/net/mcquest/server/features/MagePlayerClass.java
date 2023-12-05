package net.mcquest.server.features;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.Attitude;
import net.mcquest.core.character.Character;
import net.mcquest.core.character.NonPlayerCharacter;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MagePlayerClass implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        MageSkills.FIREBALL.onUse().subscribe(this::useFireball);
        MageSkills.ICE_BEAM.onUse().subscribe(this::useIceBeam);
        MageSkills.FLAME_WALL.onUse().subscribe(this::useFlameWall);
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

    private void useFlameWall(ActiveSkillUseEvent event) {
        double distance = 15.0;

        PlayerCharacter pc = event.getPlayerCharacter();
        Instance instance = pc.getInstance();

        RaycastHit hit = mmorpg.getPhysicsManager().raycast(
                instance,
                pc.getEyePosition(),
                pc.getLookDirection(),
                distance,
                Triggers.raycastFilter(character -> character.isDamageable(pc))
        );

        if (hit != null) {
            Triggers.character((character, hitPosition) -> {
                flameWall(event, character.getPosition());
            }).accept(hit);
        } else {
            Pos targetBlock = pc.getTargetBlockPosition(distance);
            if (targetBlock != null) {
                flameWall(event, targetBlock);
            }
        }

    }

    private void flameWall(ActiveSkillUseEvent event, Pos position) {
        long duration = 5000;
        int ticks = 10;

        PlayerCharacter pc = event.getPlayerCharacter();
        Instance instance = pc.getInstance();

        Vec hitboxSize = new Vec(5.0, 2.0, 1.0);
        Pos hitboxCenter = position.add(0.0, hitboxSize.y() / 2, 0.0);
        double yaw = -Math.toRadians(pc.getPosition().yaw());

        for (int tick = 0; tick < ticks; tick++) {
            long delay = duration * tick / ticks;
            mmorpg.getSchedulerManager().buildTask(() -> {
                ParticleEffects.fillBox(
                        instance,
                        hitboxCenter,
                        hitboxSize,
                        new Vec(0.0, yaw, 0.0),
                        Particle.FLAME,
                        3.0
                );

                Collection<Collider> hits = mmorpg.getPhysicsManager()
                        .overlapBox(instance, hitboxCenter, hitboxSize.add(0.0, 0.0, 4.0));
                hits.forEach(Triggers.character(character -> {
                    if (!character.isDamageable(pc)) {
                        return;
                    }
                    character.damage(pc, 2);
                }));

                instance.playSound(Sound.sound(
                        SoundEvent.BLOCK_AZALEA_LEAVES_PLACE,
                        Sound.Source.PLAYER,
                        2f,
                        1f
                ), hitboxCenter);
            }).delay(Duration.ofMillis(delay)).schedule();
        }
    }
}
