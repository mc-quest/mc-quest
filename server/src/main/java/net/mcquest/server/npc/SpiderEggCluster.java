package net.mcquest.server.npc;

import net.kyori.adventure.sound.Sound;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.Character;
import net.mcquest.core.character.*;
import net.mcquest.core.object.ObjectManager;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.server.constants.Instances;
import net.mcquest.server.constants.Models;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;

public class SpiderEggCluster extends NonPlayerCharacter {
    private Mmorpg mmorpg;
    public SpiderEggCluster(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Models.WOLF_SPIDER_EGG_CLUSTER));
        this.mmorpg = mmorpg;
        setName("?");
        setMaxHealth(1);
        setMass(20);
        setRemovalDelay(Duration.ofMillis(2000));
        setRespawnDuration(Duration.ofSeconds(45));
        setExperiencePoints(1);
    }

    @Override
    public Attitude getAttitude(Character other) {
        return Attitude.FRIENDLY;
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
        emitSound(Sound.sound(SoundEvent.ENTITY_SPIDER_DEATH, Sound.Source.HOSTILE, 1f, 1f));
        Pos center = getPosition();
        Pos[] positions = {
                new Pos(center.x() + 1, center.y(), center.z() + 1),
                center,
                new Pos(center.x() - 1, center.y(), center.z() - 1)
        };
        ObjectManager objectManager = mmorpg.getObjectManager();
        for (Pos position : positions) {
            objectManager.add(ObjectSpawner.of(Instances.BROODMOTHER_LAIR, position, Spider::new));
        }
    }
}