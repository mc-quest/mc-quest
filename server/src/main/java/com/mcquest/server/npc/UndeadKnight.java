package com.mcquest.server.npc;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.ai.*;
import com.mcquest.core.character.Character;
import com.mcquest.core.character.*;
import com.mcquest.core.object.ObjectSpawner;
import com.mcquest.core.physics.Triggers;
import com.mcquest.server.constants.Models;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.sound.SoundEvent;
import net.minestom.server.timer.TaskSchedule;

import java.time.Duration;

public class UndeadKnight extends NonPlayerCharacter {
    public UndeadKnight(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Models.UNDEAD_KNIGHT));

        setName("Undead Knight");
        setLevel(15);
        setMaxHealth(10);
        setMass(500);
        setRespawnDuration(Duration.ofSeconds(3));
        setRemovalDelay(Duration.ofMillis(2500));

        BlackboardKey<Character> targetKey = BlackboardKey.of("target");
        setBrain(new ActiveSelector(
                new Sequence(
                        new TaskFindClosestTarget(targetKey, 15.0),
                        new TaskPlayAnimation(CharacterAnimation.named("walk")),
                        new Parallel(
                                Parallel.Policy.REQUIRE_ONE,
                                Parallel.Policy.REQUIRE_ONE,
                                new TaskFollowTarget(targetKey, 3.0, 15.0),
                                new LoopForever(new Sequence(
                                        new TaskWait(Duration.ofMillis(750)),
                                        new TaskPlaySound(Sound.sound(SoundEvent.ENTITY_RAVAGER_STEP,
                                                Sound.Source.HOSTILE, 1f, 1f))
                                ))
                        ),
                        new TaskPlayAnimation(CharacterAnimation.named("attack_sword")),
                        new TaskWait(Duration.ofMillis(700)),
                        new TaskPlaySound(Sound.sound(SoundEvent.ENTITY_PLAYER_ATTACK_SWEEP, Sound.Source.HOSTILE, 1f
                                , 0.75f)),
                        new TaskAction(this::attack),
                        new TaskWait(Duration.ofMillis(800))
                ),
                new Sequence(
                        new TaskPlayAnimation(CharacterAnimation.named("idle")),
                        new TaskWait(Duration.ofSeconds(2)),
                        new TaskPlayAnimation(CharacterAnimation.named("walk")),
                        new Parallel(
                                Parallel.Policy.REQUIRE_ONE,
                                Parallel.Policy.REQUIRE_ONE,
                                new TaskGoToRandomPosition(10),
                                new LoopForever(new Sequence(
                                        new TaskWait(Duration.ofMillis(750)),
                                        new TaskPlaySound(Sound.sound(SoundEvent.ENTITY_RAVAGER_STEP,
                                                Sound.Source.HOSTILE, 1f, 1f))
                                ))
                        )
                )
        ));
    }

    @Override
    public Attitude getAttitude(Character other) {
        return other instanceof PlayerCharacter ? Attitude.HOSTILE : Attitude.NEUTRAL;
    }

    @Override
    public boolean isDamageable(DamageSource source) {
        return true;
    }

    @Override
    protected void onDamage(DamageSource source) {
        playSound(Sound.sound(SoundEvent.ENTITY_BLAZE_HURT, Sound.Source.HOSTILE, 1f, 1f));
    }

    @Override
    protected void onDeath(DamageSource source) {
        playAnimation(CharacterAnimation.named("death"));
        getMmorpg().getSchedulerManager()
                .buildTask(() -> playSound(Sound.sound(SoundEvent.ENTITY_GENERIC_BIG_FALL, Sound.Source.HOSTILE, 1f,
                        1f)))
                .delay(TaskSchedule.millis(1000))
                .schedule();
    }

    private boolean attack(long time) {
        Pos position = getPosition();
        Pos center = position.withY(y -> y + 2.0).add(position.direction().mul(2.0));
        Vec extents = new Vec(2.0, 3.0, 2.0);
        getMmorpg().getPhysicsManager().overlapBox(getInstance(), center, extents)
                .forEach(Triggers.character(this::attackHit));
        return true;
    }

    private void attackHit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 0.5);
        }
    }
}
