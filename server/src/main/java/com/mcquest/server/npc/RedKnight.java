package com.mcquest.server.npc;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.ai.*;
import com.mcquest.core.character.Character;
import com.mcquest.core.character.*;
import com.mcquest.core.loot.ItemPoolEntry;
import com.mcquest.core.loot.LootTable;
import com.mcquest.core.loot.Pool;
import com.mcquest.core.object.ObjectSpawner;
import com.mcquest.core.physics.Triggers;
import com.mcquest.server.constants.Items;
import com.mcquest.server.constants.Skins;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;

public class RedKnight extends NonPlayerCharacter {
    public RedKnight(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Skins.RED_KNIGHT));
        setName("Red Knight");
        setLevel(8);
        setMaxHealth(10);
        setMass(20);
        setMovementSpeed(4.0);
        setRemovalDelay(Duration.ofMillis(2000));
        setRespawnDuration(Duration.ofSeconds(45));
        setExperiencePoints(10);
        setLootTable(LootTable.builder()
                .pool(Pool.builder()
                        .entry(ItemPoolEntry.builder(Items.ADVENTURERS_SWORD).build())
                        .build())
                .build());
        getEntity().setItemInMainHand(Items.ADVENTURERS_SWORD.getItemStack());

        BlackboardKey<Character> targetKey = BlackboardKey.of("target");
        setBrain(new ActiveSelector(
                new Sequence(
                        new TaskFindClosestTarget(targetKey, 10.0),
                        new Parallel(
                                Parallel.Policy.REQUIRE_ONE,
                                Parallel.Policy.REQUIRE_ONE,
                                new TaskFollowTarget(targetKey, 2.0, 15.0),
                                new LoopForever(new Sequence(
                                        new TaskPlaySound(Sound.sound(SoundEvent.BLOCK_STONE_STEP,
                                                Sound.Source.HOSTILE, 0.75f, 1.5f)),
                                        new TaskWait(Duration.ofMillis(500))
                                ))
                        ),
                        new TaskPlayAnimation(CharacterAnimation.swingMainHand()),
                        new TaskWait(Duration.ofMillis(250)),
                        new TaskAction(this::attack),
                        new TaskWait(Duration.ofMillis(500))
                ),
                new Sequence(
                        new TaskWait(Duration.ofSeconds(2)),
                        new Parallel(
                                Parallel.Policy.REQUIRE_ONE,
                                Parallel.Policy.REQUIRE_ONE,
                                new TaskGoToRandomPosition(10),
                                new LoopForever(new Sequence(
                                        new TaskPlaySound(Sound.sound(SoundEvent.BLOCK_STONE_STEP,
                                                Sound.Source.HOSTILE, 0.75f, 1.5f)),
                                        new TaskWait(Duration.ofMillis(500))
                                ))
                        )
                )
        ));
    }

    @Override
    public Attitude getAttitude(Character other) {
        return other instanceof RedKnight ? Attitude.FRIENDLY : Attitude.HOSTILE;
    }

    @Override
    public boolean isDamageable(DamageSource source) {
        return true;
    }

    protected void onDamage(DamageSource source) {
        playSound(Sound.sound(SoundEvent.ENTITY_BLAZE_HURT, Sound.Source.HOSTILE, 0.75f, 1f));
    }

    @Override
    protected void onDeath(DamageSource source) {
        playSound(Sound.sound(SoundEvent.ENTITY_BLAZE_DEATH, Sound.Source.HOSTILE, 1f, 1f));
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
