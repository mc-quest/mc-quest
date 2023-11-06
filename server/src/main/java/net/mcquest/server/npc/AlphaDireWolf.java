package net.mcquest.server.npc;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.ai.*;
import net.mcquest.core.character.*;
import net.mcquest.core.character.Character;
import net.mcquest.core.loot.ItemPoolEntry;
import net.mcquest.core.loot.LootTable;
import net.mcquest.core.loot.Pool;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.core.physics.Collider;
import net.mcquest.core.physics.Triggers;
import net.mcquest.server.constants.Items;
import net.mcquest.server.constants.Models;
import net.mcquest.server.constants.Music;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;

public class AlphaDireWolf extends NonPlayerCharacter {
    private final Collider bossBattleBounds;

    public AlphaDireWolf(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Models.ALPHA_DIRE_WOLF));
        setName("AlphaDireWolf");
        setLevel(6);
        setMaxHealth(70);
        setMass(20);
        setMovementSpeed(10.0);
        setRemovalDelay(Duration.ofMillis(2000));
        setRespawnDuration(Duration.ofSeconds(45));
        setExperiencePoints(70);
        setLootTable(LootTable.builder()
                .pool(Pool.builder()
                        .entry(ItemPoolEntry.builder(Items.ADVENTURERS_SWORD).build())
                        .build())
                .build());

        int[] weights = {2, 2, 1};
        setBrain(ActiveSelector.of(
                Sequence.of(
                        TaskFindClosestTarget.of(25.0),
                        TaskPlayAnimation.of(CharacterAnimation.named("walk")),
                        SimpleParallel.of(
                                TaskFollowTarget.of(4.0, 15.0),
                                Sequence.of(
                                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_WOLF_STEP,
                                                Sound.Source.HOSTILE, 0.75f, 1.5f)),
                                        TaskWait.of(Duration.ofMillis(500))
                                )
                        ),
                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_WOLF_HOWL, Sound.Source.HOSTILE,
                                1f, 1f)),
                        TaskPlayAnimation.of(CharacterAnimation.named("run")),
                        TaskWait.of(Duration.ofMillis(100)),
                        RandomSelector.of(
                                weights,
                                Sequence.of(
                                        TaskPlayAnimation.of(CharacterAnimation.named("claw")),
                                        TaskWait.of(Duration.ofMillis(500)),
                                        TaskAction.of(this::claw),
                                        TaskWait.of(Duration.ofMillis(800))
                                ),
                                Sequence.of(
                                        TaskPlayAnimation.of(CharacterAnimation.named("bite")),
                                        TaskWait.of(Duration.ofMillis(500)),
                                        TaskAction.of(this::bite),
                                        TaskWait.of(Duration.ofMillis(800))
                                ),
                                Sequence.of(
                                        TaskPlayAnimation.of(CharacterAnimation.named("roar")),
                                        TaskWait.of(Duration.ofMillis(500)),
                                        TaskAction.of(this::roar),
                                        TaskWait.of(Duration.ofMillis(800))
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

        bossBattleBounds = new Collider(getInstance(), getPosition(), new Vec(50, 30, 50));
        bossBattleBounds.onCollisionEnter(Triggers.playerCharacter(this::enterBossBattle));
        bossBattleBounds.onCollisionExit(Triggers.playerCharacter(this::exitBossBattle));
    }

    @Override
    public Attitude getAttitude(Character other) {
        return (other instanceof DireWolf || other instanceof AlphaDireWolf)
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

    protected void onDamage(DamageSource source) {
        emitSound(Sound.sound(SoundEvent.ENTITY_WOLF_HURT, Sound.Source.HOSTILE, 2f, 0.75f));
    }

    @Override
    protected void onDeath(DamageSource source) {
        emitSound(Sound.sound(SoundEvent.ENTITY_WOLF_DEATH, Sound.Source.HOSTILE, 2f, 0.75f));
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
        Pos position = getPosition();
        Pos hitboxCenter = position.add(position.direction().mul(1.5));
        Vec extents = new Vec(1.0, 0.5, 1.0);
        getMmorpg().getPhysicsManager()
                .overlapBox(getInstance(), hitboxCenter, extents)
                .forEach(Triggers.character(this::clawHit));
        return true;
    }

    private void clawHit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 10);
        }
    }

    private boolean bite(long time) {
        Pos position = getPosition();
        Pos hitboxCenter = position.add(position.direction().mul(1.5));
        Vec extents = new Vec(1.0, 0.5, 1.0);
        getMmorpg().getPhysicsManager()
                .overlapBox(getInstance(), hitboxCenter, extents)
                .forEach(Triggers.character(this::biteHit));
        return true;
    }

    private void biteHit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 20);
        }
    }

    private boolean roar(long time) {
        Pos position = getPosition();
        Pos hitboxCenter = position.add(position.direction().mul(1.5));
        Vec extents = new Vec(4.0, 0.5, 4.0);
        getMmorpg().getPhysicsManager()
                .overlapBox(getInstance(), hitboxCenter, extents)
                .forEach(Triggers.character(this::roarHit));
        return true;
    }

    private void roarHit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 30);
        }
    }
}
