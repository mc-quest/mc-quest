package com.mcquest.server.npc;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.Character;
import com.mcquest.core.character.*;
import com.mcquest.core.object.ObjectSpawner;
import com.mcquest.server.constants.Models;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;

public class TrainingDummy extends NonPlayerCharacter {
    public TrainingDummy(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Models.TRAINING_DUMMY));
        setName("Training Dummy");
        setMaxHealth(10);
        setRemovalDelay(Duration.ofMillis(1500));
        setRespawnDuration(Duration.ofSeconds(30));
    }

    @Override
    public Attitude getAttitude(Character other) {
        return Attitude.NEUTRAL;
    }

    @Override
    public boolean isDamageable(DamageSource source) {
        return true;
    }

    @Override
    protected void onDamage(DamageSource source) {
        playSound(Sound.sound(SoundEvent.ENTITY_ARMOR_STAND_HIT, Sound.Source.NEUTRAL, 1f, 1f));
        playAnimation(CharacterAnimation.named("hit"));
    }

    @Override
    protected void onDeath(DamageSource killer) {
        playSound(Sound.sound(SoundEvent.ENTITY_ARMOR_STAND_BREAK, Sound.Source.NEUTRAL, 1f, 1f));
        playAnimation(CharacterAnimation.named("death"));
    }
}
