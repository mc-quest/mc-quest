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

        BlackboardKey<Character> targetKey = BlackboardKey.of("target");
        setBrain(new ActiveSelector(
                new Sequence(
                        new TaskFindClosestTarget(targetKey, 10.0),
                        new TaskPlayAnimation("walk"),
                        new Parallel(
                                Parallel.Policy.REQUIRE_ONE,
                                Parallel.Policy.REQUIRE_ONE,
                                new TaskFollowTarget(targetKey, 2.0, 15.0),
                                new LoopForever(new Sequence(
                                        new TaskPlaySound(Sound.sound(SoundEvent.ENTITY_SPIDER_STEP,
                                                Sound.Source.HOSTILE, 0.75f, 1.5f)),
                                        new TaskWait(Duration.ofMillis(500))
                                ))
                        ),
                        new TaskPlaySound(Sound.sound(SoundEvent.ENTITY_EVOKER_FANGS_ATTACK, Sound.Source.HOSTILE, 1f
                                , 1f)),
                        new TaskPlayAnimation("attack"),
                        new TaskWait(Duration.ofMillis(500)),
                        new TaskAction(this::attack),
                        new TaskWait(Duration.ofMillis(800))
                ),
                new Sequence(
                        new TaskPlayAnimation("idle"),
                        new TaskWait(Duration.ofSeconds(2)),
                        new TaskPlayAnimation("walk"),
                        new TaskGoToRandomPosition(10)
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
        playSound(Sound.sound(SoundEvent.ENTITY_SPIDER_HURT, Sound.Source.HOSTILE, 0.75f, 1f));
    }

    @Override
    protected void onDeath(DamageSource source) {
        playSound(Sound.sound(SoundEvent.ENTITY_SPIDER_DEATH, Sound.Source.HOSTILE, 1f, 1f));
        if (source instanceof PlayerCharacter pc) {
            pc.setMaxMana(100);
            pc.setMana(100);
            pc.grantExperiencePoints(100);
        }
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
            character.damage(this, 0.1);
        }
    }
}
