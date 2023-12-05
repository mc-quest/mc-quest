package net.mcquest.server.npc;

import net.kyori.adventure.sound.Sound;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.Character;
import net.mcquest.core.character.*;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.server.constants.Instances;
import net.mcquest.server.constants.Models;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;

public class SpiderEgg extends NonPlayerCharacter {
    private Mmorpg mmorpg;
    public SpiderEgg(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Models.WOLF_SPIDER_EGG));
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
        return Attitude.NEUTRAL;
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
        mmorpg.getObjectManager().add(ObjectSpawner.of(
                Instances.BROODMOTHER_LAIR,
                getPosition(),
                Spider::new
        ));
    }
}