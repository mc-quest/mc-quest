package net.mcquest.server.npc;

import net.kyori.adventure.sound.Sound;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.ai.*;
import net.mcquest.core.character.Character;
import net.mcquest.core.character.*;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.core.physics.Triggers;
import net.mcquest.server.constants.Models;
import net.mcquest.server.constants.Quests;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;

public class GoblinMinion extends NonPlayerCharacter {
    public GoblinMinion(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Models.GOBLIN_MINION));
        setName("Goblin Minion");
        setLevel(2);
        setMaxHealth(20);
        setMass(40);
        setRemovalDelay(Duration.ofMillis(2000));
        setRespawnDuration(Duration.ofSeconds(60));
        setExperiencePoints(10);

        setBrain(ActiveSelector.of(
                Sequence.of(
                        TaskFindClosestTarget.of(25.0),
                        TaskPlayAnimation.of(CharacterAnimation.named("walk")),
                        SimpleParallel.of(
                                TaskFollowTarget.of(4.0, 15.0),
                                Sequence.of(
                                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_HOGLIN_STEP,
                                                Sound.Source.HOSTILE, 0.75f, 1.5f)),
                                        TaskWait.of(Duration.ofMillis(500))
                                )
                        ),
                        TaskWait.of(Duration.ofMillis(100)),
                        TaskPlayAnimation.of(CharacterAnimation.named("attack")),
                        TaskWait.of(Duration.ofMillis(250)),
                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_HOGLIN_ATTACK,
                                Sound.Source.HOSTILE, 1f, 1f)),
                        TaskWait.of(Duration.ofMillis(250)),
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
                                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_HOGLIN_STEP,
                                                Sound.Source.HOSTILE, 0.75f, 1.5f)),
                                        TaskWait.of(Duration.ofMillis(500))
                                )
                        ),
                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_HOGLIN_AMBIENT, Sound.Source.HOSTILE, 1f, 1f))
                )
        ));
    }

    @Override
    public Attitude getAttitude(Character other) {
        return other instanceof GoblinMinion || other instanceof Grimrot ||
                other instanceof Dreadfang
                ? Attitude.FRIENDLY
                : Attitude.HOSTILE;
    }

    @Override
    public boolean isDamageable(DamageSource source) {
        return true;
    }

    protected void onDamage(DamageSource source) {
        emitSound(Sound.sound(SoundEvent.ENTITY_HOGLIN_HURT, Sound.Source.HOSTILE, 0.75f, 1f));
    }

    @Override
    protected void onDeath(DamageSource source) {
        emitSound(Sound.sound(SoundEvent.ENTITY_HOGLIN_DEATH, Sound.Source.HOSTILE, 1f, 1f));
        playAnimation(CharacterAnimation.named("death"));
    }

    private boolean attack(long time) {
        Pos hitboxCenter = getPosition().add(getLookDirection().mul(3)).withY(y -> y + 1);
        Vec extents = new Vec(3, 2, 3);
        getMmorpg().getPhysicsManager()
                .overlapBox(getInstance(), hitboxCenter, extents)
                .forEach(Triggers.character(this::attackHit));
        return true;
    }

    private void attackHit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 4);
            character.applyImpulse(getLookDirection().mul(50));
        }
    }
}