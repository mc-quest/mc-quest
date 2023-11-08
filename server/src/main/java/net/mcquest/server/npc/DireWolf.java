package net.mcquest.server.npc;

import net.kyori.adventure.sound.Sound;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.ai.*;
import net.mcquest.core.character.Character;
import net.mcquest.core.character.*;
import net.mcquest.core.loot.ItemPoolEntry;
import net.mcquest.core.loot.LootTable;
import net.mcquest.core.loot.Pool;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.core.physics.Triggers;
import net.mcquest.server.constants.Items;
import net.mcquest.server.constants.Models;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;

public class DireWolf extends NonPlayerCharacter {
    public DireWolf(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Models.DIRE_WOLF));
        setName("Dire Wolf");
        setLevel(2);
        setMaxHealth(10);
        setMass(20);
        setRemovalDelay(Duration.ofMillis(2000));
        setRespawnDuration(Duration.ofSeconds(45));
        setExperiencePoints(10);
        setLootTable(LootTable.builder()
                .pool(Pool.builder()
                        .entry(ItemPoolEntry.builder(Items.ADVENTURERS_SWORD).build())
                        .build())
                .build());

        setBrain(ActiveSelector.of(
                Sequence.of(
                        TaskFindClosestTarget.of(10.0),
                        TaskPlayAnimation.of(CharacterAnimation.named("run")),
                        SimpleParallel.of(
                                TaskFollowTarget.of(2.0, 15.0),
                                Sequence.of(
                                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_WOLF_STEP,
                                                Sound.Source.HOSTILE, 0.75f, 1.5f)),
                                        TaskWait.of(Duration.ofMillis(500))
                                )
                        ),
                        RandomSelector.of(
                                new int[]{1, 1},
                                Sequence.of(
                                        TaskPlayAnimation.of(CharacterAnimation.named("claw")),
                                        TaskWait.of(Duration.ofMillis(500)),
                                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_EVOKER_FANGS_ATTACK,
                                                Sound.Source.HOSTILE, 1f, 1f)),
                                        TaskAction.of(this::claw),
                                        TaskWait.of(Duration.ofMillis(800))
                                ),
                                Sequence.of(
                                        TaskPlayAnimation.of(CharacterAnimation.named("bite")),
                                        TaskWait.of(Duration.ofMillis(500)),
                                        TaskEmitSound.of(Sound.sound(SoundEvent.ENTITY_EVOKER_FANGS_ATTACK,
                                                Sound.Source.HOSTILE, 1f, 1f)),
                                        TaskAction.of(this::bite),
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
    }

    @Override
    public Attitude getAttitude(Character other) {
        return other instanceof DireWolf || other instanceof DirePacklord
                ? Attitude.FRIENDLY
                : Attitude.HOSTILE;
    }

    @Override
    public boolean isDamageable(DamageSource source) {
        return true;
    }

    protected void onDamage(DamageSource source) {
        emitSound(Sound.sound(SoundEvent.ENTITY_WOLF_HURT, Sound.Source.HOSTILE, 0.75f, 1f));
    }

    @Override
    protected void onDeath(DamageSource source) {
        emitSound(Sound.sound(SoundEvent.ENTITY_WOLF_DEATH, Sound.Source.HOSTILE, 1f, 1f));
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
            character.damage(this, 3);
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
            character.damage(this, 4);
        }
    }
}
