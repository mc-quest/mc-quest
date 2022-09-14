package com.mcquest.server.music;

import net.kyori.adventure.sound.Sound;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;

public class Tone {
    private final Duration time;
    private final Sound sound;

    Tone(Duration time, SoundEvent timbre, float volume, float pitch) {
        this.time = time;
        this.sound = Sound.sound(timbre, Sound.Source.MASTER, volume, pitch);
    }

    public Duration getTime() {
        return time;
    }

    public Sound getSound() {
        return sound;
    }
}
