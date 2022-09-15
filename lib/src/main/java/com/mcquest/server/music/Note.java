package com.mcquest.server.music;

import net.kyori.adventure.sound.Sound;
import net.minestom.server.sound.SoundEvent;

public class Note {
    private final double time;
    private final Sound sound;

    Note(double time, SoundEvent instrument, float volume, Pitch pitch) {
        this.time = time;
        this.sound = Sound.sound(instrument, Sound.Source.MASTER, volume, pitch.getValue());
    }

    public double getTime() {
        return time;
    }

    public Sound getSound() {
        return sound;
    }
}
