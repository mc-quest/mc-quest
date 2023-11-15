package net.mcquest.server.npc;

import net.kyori.adventure.sound.Sound;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.ai.*;
import net.mcquest.core.character.Character;
import net.mcquest.core.character.*;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.core.particle.ParticleEffects;
import net.mcquest.core.physics.Collider;
import net.mcquest.core.physics.Triggers;
import net.mcquest.server.constants.Models;
import net.mcquest.server.constants.Music;
import net.mcquest.server.constants.Quests;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.particle.Particle;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;

public class Grimrot extends NonPlayerCharacter {
    private final Collider bossBattleBounds;

    public Grimrot(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Models.GRIMROT));
        setName("Grimrot");
        setLevel(4);
        setMaxHealth(700);
        setMass(200);
        setMovementSpeed(10.0);
        setRemovalDelay(Duration.ofMillis(2000));
        setRespawnDuration(Duration.ofSeconds(60));
        setExperiencePoints(120);
        addSlayQuestObjective(Quests.DREADFANGS_REVENGE.getObjective(2));

    setBrain(ActiveSelector.of(
                Sequence.of(
                        TaskFindClosestTarget.of(25.0),
                        TaskPlayAnimation.of(CharacterAnimation.named("run")),
                        SimpleParallel.of(
                                TaskFollowTarget.of(4.0, 15.0),
                                Sequence.of(
                                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_HOGLIN_STEP,
                                                Sound.Source.HOSTILE, 0.75f, 1.5f)),
                                        TaskWait.of(Duration.ofMillis(500))
                                )
                        ),
                        TaskWait.of(Duration.ofMillis(100)),
                        RandomSelector.of(
                                new int[]{1, 1, 1},
                                Sequence.of(
                                        TaskPlayAnimation.of(CharacterAnimation.named("ground slam")),
                                        TaskWait.of(Duration.ofMillis(250)),
                                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_DRAGON_FIREBALL_EXPLODE,
                                                Sound.Source.HOSTILE, 1f, 1f)),
                                        TaskWait.of(Duration.ofMillis(250)),
                                        TaskAction.of(this::groundSlam),
                                        TaskWait.of(Duration.ofMillis(800))
                                ),
                                Sequence.of(
                                        TaskPlayAnimation.of(CharacterAnimation.named("roadhog heal")),
                                        TaskWait.of(Duration.ofMillis(300)),
                                        TaskWait.of(Duration.ofMillis(250)),
                                        TaskAction.of(this::roadhogHeal),
                                        TaskWait.of(Duration.ofMillis(800))
                                ),
                                Sequence.of(
                                        TaskPlayAnimation.of(CharacterAnimation.named("swing")),
                                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_PLAYER_ATTACK_SWEEP,
                                                Sound.Source.HOSTILE, 1.5f, 0.75f)),
                                        TaskWait.of(Duration.ofMillis(900)),
                                        TaskAction.of(this::swing),
                                        TaskWait.of(Duration.ofMillis(1000))
                                ),
                                Sequence.of(
                                        TaskPlayAnimation.of(CharacterAnimation.named("ground hop")),
                                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_DRAGON_FIREBALL_EXPLODE,
                                                Sound.Source.HOSTILE, 1.5f, 0.75f)),
                                        TaskWait.of(Duration.ofMillis(900)),
                                        TaskAction.of(this::groundHop),
                                        TaskWait.of(Duration.ofMillis(1000))
                                )
                        )
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
                        )
                )
        ));

        bossBattleBounds = new Collider(getInstance(), getPosition(), new Vec(25, 10, 25));
        bossBattleBounds.onCollisionEnter(Triggers.playerCharacter(this::enterBossBattle));
        bossBattleBounds.onCollisionExit(Triggers.playerCharacter(this::exitBossBattle));
    }

    @Override
    public Attitude getAttitude(Character other) {
        return (other instanceof GoblinMinion || other instanceof Grimrot)
                ? Attitude.FRIENDLY
                : Attitude.HOSTILE;
    }

    @Override
    public boolean isDamageable(DamageSource source) {
        return true;
    }

    @Override
    protected void onSpawn() {
        getMmorpg().getPhysicsManager().addCollider(bossBattleBounds);
    }

    @Override
    protected void onDespawn() {
        bossBattleBounds.remove();
    }

    @Override
    protected void onChangePosition(Pos position) {
        bossBattleBounds.setCenter(position);
    }

    @Override
    protected void onDeath(DamageSource source) {
        emitSound(Sound.sound(SoundEvent.ENTITY_HOGLIN_DEATH, Sound.Source.HOSTILE, 2f, 0.6f));
        playAnimation(CharacterAnimation.named("death"));
    }

    private void enterBossBattle(PlayerCharacter pc) {
        getBossHealthBar().addViewer(pc);
        pc.getMusicPlayer().setSong(Music.GOBLIN_BATTLE);
    }

    private void exitBossBattle(PlayerCharacter pc) {
        getBossHealthBar().removeViewer(pc);
        pc.getMusicPlayer().setSong(Music.KINGS_DEATH_ROW);
    }

    private boolean groundSlam(long time) {
        Pos hitboxCenter = getPosition().add(getLookDirection().mul(3)).withY(y -> y + 1.5);
        Vec extents = new Vec(3.25, 3, 3.25);
        getMmorpg().getPhysicsManager()
                .overlapBox(getInstance(), hitboxCenter, extents)
                .forEach(Triggers.character(this::groundSlamHit));
        return true;
    }

    private void groundSlamHit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 15);
            character.applyImpulse(getLookDirection().mul(200));
        }
    }

    private boolean roadhogHeal(long time) {
        heal(this, 25);
        return true;
    }

    private boolean swing(long time) {
        Pos hitboxCenter = getPosition().add(getLookDirection().mul(3)).withY(y -> y + 1.5);
        Vec extents = new Vec(3.25, 3, 3.25);
        getMmorpg().getPhysicsManager()
                .overlapBox(getInstance(), hitboxCenter, extents)
                .forEach(Triggers.character(this::swingHit));
        return true;
    }

    private void swingHit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 8);
            character.applyImpulse(getLookDirection().mul(200));
        }
    }

    private boolean groundHop(long time) {
        Pos hitboxCenter = getPosition().add(getLookDirection().mul(3)).withY(y -> y + 1.5);
        Vec extents = new Vec(3.25, 3, 3.25);
        getMmorpg().getPhysicsManager()
                .overlapBox(getInstance(), hitboxCenter, extents)
                .forEach(Triggers.character(this::swingHit));
        return true;
    }

    private void groundHopHit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 15);
            character.applyImpulse(getLookDirection().mul(200));
        }
    }
}