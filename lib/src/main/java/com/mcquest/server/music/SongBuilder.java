package com.mcquest.server.music;

import net.minestom.server.sound.SoundEvent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SongBuilder {
    private final MusicManager musicManager;
    private final int id;
    private final Duration duration;
    private final List<Tone> tones;

    SongBuilder(MusicManager musicManager, int id, Duration duration) {
        this.musicManager = musicManager;
        this.id = id;
        this.duration = duration;
        this.tones = new ArrayList<>();
    }

    public SongBuilder tone(Duration time, SoundEvent timbre, float volume, float pitch) {
        tones.add(new Tone(time, timbre, volume, pitch));
        return this;
    }

    public Song build() {
        Song song = new Song(id, duration, tones.toArray(new Tone[0]));
        musicManager.register(song);
        return song;
    }
}
