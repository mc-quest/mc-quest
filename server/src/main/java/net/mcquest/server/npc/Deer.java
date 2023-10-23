package net.mcquest.server.npc;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.ai.*;
import net.mcquest.core.character.CharacterAnimation;
import net.mcquest.core.character.CharacterModel;
import net.mcquest.core.character.DamageSource;
import net.mcquest.core.character.NonPlayerCharacter;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.server.constants.Models;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;

public class Deer extends NonPlayerCharacter {
    private boolean panic;

    public Deer(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Models.DEER));

        setName("Deer");
        setLevel(1);
        setMaxHealth(10);
        setMass(60);
        setMovementSpeed(0.6);
        setRemovalDelay(Duration.ofMillis(2000));
        setRespawnDuration(Duration.ofSeconds(60));

        setBrain(new ActiveSelector(
                // Run if panicking.
                new Sequence(
                        new TaskAction(this::isPanicking),
                        new TaskPlayAnimation(CharacterAnimation.named("run")),
                        new SimpleParallel(
                                new LoopNTimes(3, new TaskGoToRandomPosition(10)),
                                new Sequence(
                                        new TaskEmitSound(Sound.sound(SoundEvent.BLOCK_GRASS_STEP,
                                                Sound.Source.AMBIENT, 1f, 1f)),
                                        new TaskWait(Duration.ofMillis(250))
                                )
                        ),
                        new TaskAction(this::endPanic)
                ),
                // Forage.
                new Sequence(
                        new TaskPlayAnimation(CharacterAnimation.named("walk")),
                        new SimpleParallel(
                                new TaskGoToRandomPosition(10),
                                new Sequence(
                                        new TaskEmitSound(Sound.sound(SoundEvent.BLOCK_GRASS_STEP,
                                                Sound.Source.AMBIENT, 1f, 1f)),
                                        new TaskWait(Duration.ofMillis(750))
                                )
                        ),
                        new TaskPlayAnimation(CharacterAnimation.named("idle")),
                        new TaskWait(Duration.ofSeconds(2)),
                        new TaskPlayAnimation(CharacterAnimation.named("eat")),
                        new TaskWait(Duration.ofMillis(1500)),
                        new LoopNTimes(9, new Sequence(
                                new TaskEmitSound(Sound.sound(SoundEvent.ENTITY_GENERIC_EAT, Sound.Source.AMBIENT,
                                        0.2f, 1f)),
                                new TaskWait(Duration.ofMillis(500))
                        )),
                        new TaskWait(Duration.ofSeconds(2)),
                        new TaskPlayAnimation(CharacterAnimation.named("idle")),
                        new TaskWait(Duration.ofSeconds(2))
                )
        ));

        panic = false;
    }

    @Override
    public boolean isDamageable(DamageSource source) {
        return true;
    }

    @Override
    protected void onDamage(DamageSource source) {
        emitSound(Sound.sound(SoundEvent.ENTITY_MULE_HURT, Sound.Source.AMBIENT, 1f, 1f));
        beginPanic();
    }

    @Override
    protected void onDeath(DamageSource killer) {
        playAnimation(CharacterAnimation.named("death"));
        emitSound(Sound.sound(SoundEvent.ENTITY_MULE_DEATH, Sound.Source.AMBIENT, 1f, 1f));
    }

    private void beginPanic() {
        panic = true;
        setMovementSpeed(6.0);
    }

    private boolean isPanicking(long time) {
        return panic;
    }

    private boolean endPanic(long time) {
        panic = false;
        setMovementSpeed(0.6);
        return true;
    }
}
