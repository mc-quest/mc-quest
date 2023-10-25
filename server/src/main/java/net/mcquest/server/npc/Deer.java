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

        setBrain(ActiveSelector.of(
                // Run if panicking.
                Sequence.of(
                        TaskAction.of(this::isPanicking),
                        TaskPlayAnimation.of(CharacterAnimation.named("run")),
                        SimpleParallel.of(
                                LoopNTimes.of(3, TaskGoToRandomPosition.of(10)),
                                Sequence.of(
                                        TaskEmitSound.of(Sound.sound(SoundEvent.BLOCK_GRASS_STEP,
                                                Sound.Source.AMBIENT, 1f, 1f)),
                                        TaskWait.of(Duration.ofMillis(250))
                                )
                        ),
                        TaskAction.of(this::endPanic)
                ),
                // Forage.
                Sequence.of(
                        TaskPlayAnimation.of(CharacterAnimation.named("walk")),
                        SimpleParallel.of(
                                TaskGoToRandomPosition.of(10),
                                Sequence.of(
                                        TaskEmitSound.of(Sound.sound(SoundEvent.BLOCK_GRASS_STEP,
                                                Sound.Source.AMBIENT, 1f, 1f)),
                                        TaskWait.of(Duration.ofMillis(750))
                                )
                        ),
                        TaskPlayAnimation.of(CharacterAnimation.named("idle")),
                        TaskWait.of(Duration.ofSeconds(2)),
                        TaskPlayAnimation.of(CharacterAnimation.named("eat")),
                        TaskWait.of(Duration.ofMillis(1500)),
                        LoopNTimes.of(9, Sequence.of(
                                TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_GENERIC_EAT, Sound.Source.AMBIENT,
                                        0.2f, 1f)),
                                TaskWait.of(Duration.ofMillis(500))
                        )),
                        TaskWait.of(Duration.ofSeconds(2)),
                        TaskPlayAnimation.of(CharacterAnimation.named("idle")),
                        TaskWait.of(Duration.ofSeconds(2))
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
