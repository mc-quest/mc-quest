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
import net.minestom.server.entity.metadata.other.FallingBlockMeta;
import net.minestom.server.instance.block.Block;
import net.minestom.server.particle.Particle;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class MagePlayerClass implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        MageSkills.FIREBALL.onUse().subscribe(this::useFireball);
        MageSkills.ICE_BEAM.onUse().subscribe(this::useIceBeam);
        MageSkills.CHAIN_LIGHTNING.onUse().subscribe(this::useChainLightning);
        MageSkills.FROZEN_ORB.onUse().subscribe(this::useFrozenOrb);
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

    private void useChainLightning(ActiveSkillUseEvent event) {
        int chains = 3;
        double chainRange = 10.0;

        PlayerCharacter pc = event.getPlayerCharacter();
        Instance instance = pc.getInstance();
        Pos origin = pc.getWeaponPosition();
        Vec direction = pc.getEyePosition()
                .add(pc.getLookDirection().mul(chainRange))
                .sub(origin)
                .asVec()
                .normalize();

        instance.playSound(Sound.sound(
                SoundEvent.ITEM_TRIDENT_RIPTIDE_3,
                Sound.Source.PLAYER,
                2f,
                2f
        ), origin);

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

        instance.playSound(Sound.sound(
                SoundEvent.ITEM_TRIDENT_RIPTIDE_3,
                Sound.Source.PLAYER,
                2f,
                2f
        ), chainOrigin);

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
  
    private void useFrozenOrb(ActiveSkillUseEvent event) {
        double orbSpeed = 10.0;
        double shardSpeed = 15.0;
        long orbDuration = 1000L;
        long shardDuration = 300L;
        int pulses = 5;
        int shards = 8;

        PlayerCharacter pc = event.getPlayerCharacter();
        Instance instance = pc.getInstance();

        Pos orbPosition = pc.getWeaponPosition().add(pc.getLookDirection().mul(1f));
        Vec orbVelocity = pc.getLookDirection().mul(orbSpeed);

        Entity orbEntity = new Entity(EntityType.FALLING_BLOCK);
        FallingBlockMeta orbMeta = (FallingBlockMeta) orbEntity.getEntityMeta();
        orbMeta.setBlock(Block.ICE);
        orbEntity.setVelocity(orbVelocity);
        orbEntity.setNoGravity(true);
        orbEntity.setInstance(instance, orbPosition);

        instance.playSound(Sound.sound(
                SoundEvent.BLOCK_FIRE_EXTINGUISH,
                Sound.Source.PLAYER,
                1f,
                1f
        ), orbPosition);

        for (int pulse = 0; pulse < pulses; pulse++) {
            long delay = (long) (orbDuration * 0.9 * (pulse + 1.0) / pulses);
            mmorpg.getSchedulerManager().buildTask(() -> {
                Pos shardPosition = orbEntity.getPosition().add(0.0, 0.5, 0.0);

                Set<Character> hits = new HashSet<>();

                for (int shard = 0; shard < shards; shard++) {
                    double angle = 2 * Math.PI * shard / shards;
                    Vec shardVelocity = orbVelocity.withY(0).normalize().rotateAroundY(angle).mul(shardSpeed);

                    Vec shardHitboxSize = new Vec(0.5, 0.5, 0.5);
                    Collider shardHitbox = new Collider(instance, shardPosition, shardHitboxSize);

                    Entity shardEntity = new Entity(EntityType.SNOWBALL){
                        @Override
                        public void tick(long time) {
                            super.tick(time);
                            shardHitbox.setCenter(getPosition());
                        }
                    };
                    shardEntity.setVelocity(shardVelocity);
                    shardEntity.setNoGravity(true);
                    shardEntity.setInstance(instance, shardPosition);

                    shardHitbox.onCollisionEnter(Triggers.character(character -> {
                        if (!character.isDamageable(pc) || hits.contains(character)) {
                            return;
                        }
                        hits.add(character);
                        character.damage(pc, 5.0);
                        shardEntity.remove();
                        shardHitbox.remove();
                    }));
                    mmorpg.getPhysicsManager().addCollider(shardHitbox);

                    mmorpg.getSchedulerManager().buildTask(() -> {
                        shardEntity.remove();
                        shardHitbox.remove();
                    }).delay(Duration.ofMillis(shardDuration)).schedule();
                }

                instance.playSound(Sound.sound(
                        SoundEvent.BLOCK_SOUL_SAND_STEP,
                        Sound.Source.PLAYER,
                        2f,
                        1f
                ), shardPosition);
            }).delay(Duration.ofMillis(delay)).schedule();
        }

        mmorpg.getSchedulerManager().buildTask(orbEntity::remove)
                .delay(Duration.ofMillis(orbDuration)).schedule();
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
