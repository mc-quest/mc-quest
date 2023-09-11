package com.mcquest.server.npc;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.ai.*;
import com.mcquest.core.character.CharacterAnimation;
import com.mcquest.core.character.CharacterModel;
import com.mcquest.core.character.DamageSource;
import com.mcquest.core.character.NonPlayerCharacter;
import com.mcquest.core.object.ObjectSpawner;
import com.mcquest.server.constants.Models;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.attribute.Attribute;
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
        setRemovalDelay(Duration.ofMillis(2000));
        setRespawnDuration(Duration.ofSeconds(60));

        getEntity().getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.03f);
        setBrain(new ActiveSelector(
                // Run if panicking.
                new Sequence(
                        new TaskAction(this::isPanicking),
                        new TaskPlayAnimation(CharacterAnimation.named("run")),
                        new Parallel(
                                Parallel.Policy.REQUIRE_ONE,
                                Parallel.Policy.REQUIRE_ONE,
                                new LoopNTimes(3, new TaskGoToRandomPosition(10)),
                                new LoopForever(new Sequence(
                                        new TaskPlaySound(Sound.sound(SoundEvent.BLOCK_GRASS_STEP,
                                                Sound.Source.AMBIENT, 1f, 1f)),
                                        new TaskWait(Duration.ofMillis(250))
                                ))
                        ),
                        new TaskAction(this::endPanic)
                ),
                // Forage.
                new Sequence(
                        new TaskPlayAnimation(CharacterAnimation.named("walk")),
                        new Parallel(
                                Parallel.Policy.REQUIRE_ONE,
                                Parallel.Policy.REQUIRE_ONE,
                                new TaskGoToRandomPosition(10),
                                new LoopForever(new Sequence(
                                        new TaskPlaySound(Sound.sound(SoundEvent.BLOCK_GRASS_STEP,
                                                Sound.Source.AMBIENT, 1f, 1f)),
                                        new TaskWait(Duration.ofMillis(750))
                                ))
                        ),
                        new TaskPlayAnimation(CharacterAnimation.named("idle")),
                        new TaskWait(Duration.ofSeconds(2)),
                        new TaskPlayAnimation(CharacterAnimation.named("eat")),
                        new TaskWait(Duration.ofMillis(1500)),
                        new LoopNTimes(9, new Sequence(
                                new TaskPlaySound(Sound.sound(SoundEvent.ENTITY_GENERIC_EAT, Sound.Source.AMBIENT,
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
        playSound(Sound.sound(SoundEvent.ENTITY_MULE_HURT, Sound.Source.AMBIENT, 1f, 1f));
        beginPanic();
    }

    @Override
    protected void onDeath(DamageSource killer) {
        playAnimation(CharacterAnimation.named("death"));
        playSound(Sound.sound(SoundEvent.ENTITY_MULE_DEATH, Sound.Source.AMBIENT, 1f, 1f));
    }

    private void beginPanic() {
        panic = true;
        getEntity().getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.3f);
    }

    private boolean isPanicking(long time) {
        return panic;
    }

    private boolean endPanic(long time) {
        panic = false;
        getEntity().getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(0.03f);
        return true;
    }
}
