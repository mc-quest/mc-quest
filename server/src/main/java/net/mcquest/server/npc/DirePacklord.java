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

public class DirePacklord extends NonPlayerCharacter {
    private final Collider bossBattleBounds;

    public DirePacklord(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Models.DIRE_PACKLORD));
        setName("Dire Packlord");
        setLevel(3);
        setMaxHealth(400);
        setMass(200);
        setMovementSpeed(10.0);
        setRemovalDelay(Duration.ofMillis(2000));
        setRespawnDuration(Duration.ofSeconds(60));
        setExperiencePoints(70);
        addSlayQuestObjective(Quests.CANINE_CARNAGE.getObjective(1));

        setBrain(ActiveSelector.of(
                Sequence.of(
                        TaskFindClosestTarget.of(25.0),
                        TaskPlayAnimation.of(CharacterAnimation.named("run")),
                        SimpleParallel.of(
                                TaskFollowTarget.of(4.0, 15.0),
                                Sequence.of(
                                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_WOLF_STEP,
                                                Sound.Source.HOSTILE, 0.75f, 1.5f)),
                                        TaskWait.of(Duration.ofMillis(500))
                                )
                        ),
                        TaskWait.of(Duration.ofMillis(100)),
                        RandomSelector.of(
                                new int[]{1, 1, 1},
                                Sequence.of(
                                        TaskPlayAnimation.of(CharacterAnimation.named("claw")),
                                        TaskWait.of(Duration.ofMillis(250)),
                                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_PLAYER_ATTACK_SWEEP,
                                                Sound.Source.HOSTILE, 1f, 1f)),
                                        TaskWait.of(Duration.ofMillis(250)),
                                        TaskAction.of(this::claw),
                                        TaskWait.of(Duration.ofMillis(800))
                                ),
                                Sequence.of(
                                        TaskPlayAnimation.of(CharacterAnimation.named("bite")),
                                        TaskWait.of(Duration.ofMillis(300)),
                                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_EVOKER_FANGS_ATTACK,
                                                Sound.Source.HOSTILE, 1f, 1f)),
                                        TaskWait.of(Duration.ofMillis(250)),
                                        TaskAction.of(this::bite),
                                        TaskWait.of(Duration.ofMillis(800))
                                ),
                                Sequence.of(
                                        TaskPlayAnimation.of(CharacterAnimation.named("roar")),
                                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_RAVAGER_ROAR,
                                                Sound.Source.HOSTILE, 1.5f, 0.75f)),
                                        TaskWait.of(Duration.ofMillis(900)),
                                        TaskAction.of(this::roar),
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
                                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_WOLF_STEP,
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
        return (other instanceof DireWolf || other instanceof DirePacklord)
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
        emitSound(Sound.sound(SoundEvent.ENTITY_WOLF_DEATH, Sound.Source.HOSTILE, 2f, 0.6f));
        playAnimation(CharacterAnimation.named("death"));
    }

    private void enterBossBattle(PlayerCharacter pc) {
        getBossHealthBar().addViewer(pc);
        pc.getMusicPlayer().setSong(Music.ALPHA_WOLF_BATTLE);
    }

    private void exitBossBattle(PlayerCharacter pc) {
        getBossHealthBar().removeViewer(pc);
        pc.getMusicPlayer().setSong(Music.WOLF_DEN);
    }

    private boolean claw(long time) {
        Pos hitboxCenter = getPosition().add(getLookDirection().mul(3)).withY(y -> y + 1.5);
        Vec extents = new Vec(3.25, 3, 3.25);
        getMmorpg().getPhysicsManager()
                .overlapBox(getInstance(), hitboxCenter, extents)
                .forEach(Triggers.character(this::clawHit));
        return true;
    }

    private void clawHit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 5);
            character.applyImpulse(getLookDirection().mul(200));
        }
    }

    private boolean bite(long time) {
        Pos hitboxCenter = getPosition().add(getLookDirection().mul(3)).withY(y -> y + 1.5);
        Vec extents = new Vec(3.25, 3, 3.25);
        getMmorpg().getPhysicsManager()
                .overlapBox(getInstance(), hitboxCenter, extents)
                .forEach(Triggers.character(this::biteHit));
        return true;
    }

    private void biteHit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 6);
            character.applyImpulse(getLookDirection().mul(100));
        }
    }

    private boolean roar(long time) {
        Pos position = getPosition();
        Pos hitboxCenter = position.add(position.direction().mul(1.5));
        Vec extents = new Vec(7, 4, 7);
        getMmorpg().getPhysicsManager()
                .overlapBox(getInstance(), hitboxCenter, extents)
                .forEach(Triggers.character(this::roarHit));
        ParticleEffects.particle(getInstance(), hitboxCenter, Particle.FLASH);
        return true;
    }

    private void roarHit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 6);
            character.applyImpulse(getLookDirection().mul(2000));
        }
    }
}
