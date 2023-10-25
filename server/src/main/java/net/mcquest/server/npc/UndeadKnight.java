package net.mcquest.server.npc;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.ai.*;
import net.mcquest.core.character.*;
import net.mcquest.core.character.Character;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.core.physics.Triggers;
import net.mcquest.server.constants.Models;
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
        setMaxHealth(200);
        setMass(500);
        setMovementSpeed(6.0);
        setRespawnDuration(Duration.ofSeconds(3));
        setRemovalDelay(Duration.ofMillis(2500));

        setBrain(ActiveSelector.of(
                Sequence.of(
                        TaskFindClosestTarget.of(15.0),
                        TaskPlayAnimation.of(CharacterAnimation.named("walk")),
                        SimpleParallel.of(
                                TaskFollowTarget.of(3.0, 15.0),
                                Sequence.of(
                                        TaskWait.of(Duration.ofMillis(750)),
                                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_RAVAGER_STEP,
                                                Sound.Source.HOSTILE, 1f, 1f))
                                )
                        ),
                        TaskPlayAnimation.of(CharacterAnimation.named("attack_sword")),
                        TaskWait.of(Duration.ofMillis(700)),
                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_PLAYER_ATTACK_SWEEP,
                                Sound.Source.HOSTILE, 1f, 0.75f)),
                        TaskAction.of(this::attack),
                        TaskWait.of(Duration.ofMillis(800))
                ),
                Sequence.of(
                        TaskPlayAnimation.of(CharacterAnimation.named("idle")),
                        TaskWait.of(Duration.ofSeconds(2)),
                        TaskPlayAnimation.of(CharacterAnimation.named("walk")),
                        SimpleParallel.of(
                                TaskGoToRandomPosition.of(10),
                                Sequence.of(
                                        TaskWait.of(Duration.ofMillis(750)),
                                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_RAVAGER_STEP,
                                                Sound.Source.HOSTILE, 1f, 1f))
                                )
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
        emitSound(Sound.sound(SoundEvent.ENTITY_BLAZE_HURT, Sound.Source.HOSTILE, 1f, 1f));
    }

    @Override
    protected void onDeath(DamageSource source) {
        playAnimation(CharacterAnimation.named("death"));
        getMmorpg().getSchedulerManager()
                .buildTask(() -> emitSound(Sound.sound(SoundEvent.ENTITY_GENERIC_BIG_FALL, Sound.Source.HOSTILE, 1f,
                        1f)))
                .delay(TaskSchedule.millis(1000))
                .schedule();
    }

    private boolean attack(long time) {
        Pos position = getPosition();
        Pos center = position.withY(y -> y + 3.0).add(position.direction().mul(3.0));
        Vec extents = new Vec(2.5, 3.5, 2.5);
        getMmorpg().getPhysicsManager().overlapBox(getInstance(), center, extents)
                .forEach(Triggers.character(this::attackHit));
        return true;
    }

    private void attackHit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 100.0);
            character.applyImpulse(getPosition().direction().mul(250.0));
        }
    }
}
