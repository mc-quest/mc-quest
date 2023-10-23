package net.mcquest.server.npc;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.ai.*;
import net.mcquest.core.character.*;
import net.mcquest.core.character.Character;
import net.mcquest.core.loot.ItemPoolEntry;
import net.mcquest.core.loot.LootTable;
import net.mcquest.core.loot.Pool;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.core.physics.Triggers;
import net.mcquest.server.constants.Items;
import net.mcquest.server.constants.Models;
import net.mcquest.server.constants.Quests;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;

public class Spider extends NonPlayerCharacter {
    public Spider(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Models.WOLF_SPIDER));
        setName("Spider");
        setLevel(4);
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

        setBrain(new ActiveSelector(
                new Sequence(
                        new TaskFindClosestTarget(10.0),
                        new TaskPlayAnimation(CharacterAnimation.named("walk")),
                        new SimpleParallel(
                                new TaskFollowTarget(2.0, 15.0),
                                new Sequence(
                                        new TaskEmitSound(Sound.sound(SoundEvent.ENTITY_SPIDER_STEP,
                                                Sound.Source.HOSTILE, 0.75f, 1.5f)),
                                        new TaskWait(Duration.ofMillis(500))
                                )
                        ),
                        new TaskEmitSound(Sound.sound(SoundEvent.ENTITY_EVOKER_FANGS_ATTACK, Sound.Source.HOSTILE, 1f
                                , 1f)),
                        new TaskPlayAnimation(CharacterAnimation.named("attack")),
                        new TaskWait(Duration.ofMillis(500)),
                        new TaskAction(this::attack),
                        new TaskWait(Duration.ofMillis(800))
                ),
                new Sequence(
                        new TaskPlayAnimation(CharacterAnimation.named("idle")),
                        new TaskWait(Duration.ofSeconds(2)),
                        new TaskPlayAnimation(CharacterAnimation.named("walk")),
                        new SimpleParallel(
                                new TaskGoToRandomPosition(10),
                                new Sequence(
                                        new TaskEmitSound(Sound.sound(SoundEvent.ENTITY_SPIDER_STEP,
                                                Sound.Source.HOSTILE, 0.75f, 1.5f)),
                                        new TaskWait(Duration.ofMillis(500))
                                )
                        )
                )
        ));
    }

    @Override
    public Attitude getAttitude(Character other) {
        return other instanceof Spider ? Attitude.FRIENDLY : Attitude.HOSTILE;
    }

    @Override
    public boolean isDamageable(DamageSource source) {
        return true;
    }

    protected void onDamage(DamageSource source) {
        emitSound(Sound.sound(SoundEvent.ENTITY_SPIDER_HURT, Sound.Source.HOSTILE, 0.75f, 1f));
    }

    @Override
    protected void onDeath(DamageSource source) {
        if (source instanceof PlayerCharacter pc) Quests.ARACHNOPHOBIA.getObjective(0).addProgress(pc);
        emitSound(Sound.sound(SoundEvent.ENTITY_SPIDER_DEATH, Sound.Source.HOSTILE, 1f, 1f));
    }

    private boolean attack(long time) {
        Pos position = getPosition();
        Pos hitboxCenter = position.add(position.direction().mul(1.5));
        Vec extents = new Vec(1.0, 0.5, 1.0);
        getMmorpg().getPhysicsManager()
                .overlapBox(getInstance(), hitboxCenter, extents)
                .forEach(Triggers.character(this::attackHit));
        return true;
    }

    private void attackHit(Character character) {
        if (getAttitude(character) == Attitude.HOSTILE && character.isDamageable(this)) {
            character.damage(this, 20);
        }
    }
}
