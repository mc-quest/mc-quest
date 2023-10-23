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
import net.mcquest.server.constants.Skins;
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

        setBrain(new ActiveSelector(
                new Sequence(
                        new TaskFindClosestTarget(10.0),
                        new SimpleParallel(
                                new TaskFollowTarget(2.0, 15.0),
                                new Sequence(
                                        new TaskEmitSound(Sound.sound(SoundEvent.BLOCK_STONE_STEP,
                                                Sound.Source.HOSTILE, 0.75f, 1.5f)),
                                        new TaskWait(Duration.ofMillis(500))
                                )
                        ),
                        new TaskPlayAnimation(CharacterAnimation.swingMainHand()),
                        new TaskWait(Duration.ofMillis(250)),
                        new TaskAction(this::attack),
                        new TaskWait(Duration.ofMillis(500))
                ),
                new Sequence(
                        new TaskWait(Duration.ofSeconds(2)),
                        new SimpleParallel(
                                new TaskGoToRandomPosition(10),
                                new Sequence(
                                        new TaskEmitSound(Sound.sound(SoundEvent.BLOCK_STONE_STEP,
                                                Sound.Source.HOSTILE, 0.75f, 1.5f)),
                                        new TaskWait(Duration.ofMillis(500))
                                )
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
        emitSound(Sound.sound(SoundEvent.ENTITY_BLAZE_HURT, Sound.Source.HOSTILE, 0.75f, 1f));
    }

    @Override
    protected void onDeath(DamageSource source) {
        emitSound(Sound.sound(SoundEvent.ENTITY_BLAZE_DEATH, Sound.Source.HOSTILE, 1f, 1f));
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
