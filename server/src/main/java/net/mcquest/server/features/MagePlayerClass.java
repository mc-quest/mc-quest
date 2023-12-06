package net.mcquest.server.features;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.Character;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.ActiveSkillUseEvent;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.loot.ItemPoolEntry;
import net.mcquest.core.loot.LootChest;
import net.mcquest.core.loot.LootTable;
import net.mcquest.core.loot.Pool;
import net.mcquest.core.model.CoreModels;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.core.particle.ParticleEffects;
import net.mcquest.core.physics.Collider;
import net.mcquest.core.physics.Projectile;
import net.mcquest.core.physics.RaycastHit;
import net.mcquest.core.physics.Triggers;
import net.mcquest.server.constants.Instances;
import net.mcquest.server.constants.Items;
import net.mcquest.server.constants.MageSkills;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.particle.Particle;
import net.minestom.server.sound.SoundEvent;
import team.unnamed.hephaestus.Model;

import java.time.Duration;

public class MagePlayerClass implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        MageSkills.FIREBALL.onUse().subscribe(this::useFireball);
        MageSkills.ICE_BEAM.onUse().subscribe(this::useIceBeam);
    }

    public void useFireball(ActiveSkillUseEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();

        mmorpg.getObjectManager().spawn(ObjectSpawner.of(
                pc.getInstance(),
                pc.getPosition(),
                ((mmorpg, spawner) -> createMageFireball(mmorpg, spawner, EntityType.FIREBALL, pc))
        ));
    }

    private Projectile createMageFireball(Mmorpg mmorpg, ObjectSpawner spawner, EntityType type, PlayerCharacter pc) {
        double damageAmount = 6.0;
        double maxDistance = 20.0;
        double fireballSpeed = 20.0;
        Instance instance = spawner.getInstance();
        Vec hitboxSize = new Vec(1f, 1f, 1f);
        Vec fireballVelocity = pc.getLookDirection().mul(fireballSpeed);
        Pos startPosition = pc.getWeaponPosition().add(pc.getLookDirection().mul(1f));

        Projectile fireball = new Projectile(mmorpg, spawner, type, startPosition, maxDistance);
        fireball.setVelocity(fireballVelocity);
        fireball.setHitboxSize(hitboxSize);
        fireball.onHit(Triggers.character(character -> {
            if (!character.isDamageable(pc)) {
                return;
            }

            character.damage(pc, damageAmount);

            ParticleEffects.particle(spawner.getInstance(), startPosition, Particle.EXPLOSION);

            instance.playSound(Sound.sound(
                    SoundEvent.ENTITY_DRAGON_FIREBALL_EXPLODE,
                    Sound.Source.PLAYER,
                    1f,
                    1f
            ), startPosition);

            fireball.remove();
        }));
        fireball.onStuck(() -> {
            instance.playSound(Sound.sound(
                    SoundEvent.ENTITY_DRAGON_FIREBALL_EXPLODE,
                    Sound.Source.PLAYER,
                    1f,
                    1f
            ), startPosition);

            fireball.remove();
        });

        pc.getInstance().playSound(Sound.sound(
                SoundEvent.BLOCK_FIRE_EXTINGUISH,
                Sound.Source.PLAYER,
                1f,
                1f
        ), startPosition);


        return fireball;
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
}
