package com.mcquest.server.music;

import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SongBuilder {
    private final MusicManager musicManager;
    private final int id;
    private final double duration;
    private final double beatsPerMinute;
    private final List<Note> notes;
    private SoundEvent instrument;
    private float volume;

    SongBuilder(MusicManager musicManager, int id, double duration, double beatsPerMinute) {
        this.musicManager = musicManager;
        this.id = id;
        this.duration = duration;
        this.beatsPerMinute = beatsPerMinute;
        this.notes = new ArrayList<>();
        this.instrument = null;
        this.volume = 1f;
    }

    public SongBuilder instrument(@NotNull SoundEvent instrument) {
        this.instrument = instrument;
        return this;
    }

    public SongBuilder volume(float volume) {
        this.volume = volume;
        return this;
    }

    public SongBuilder note(double time, Pitch pitch) {
        if (instrument == null) {
            throw new IllegalStateException("You need to specify an instrument");
        }
        if (time > duration) {
            throw new IllegalArgumentException("time > duration");
        }
        notes.add(new Note(time, instrument, volume, pitch));
        return this;
    }

    public Song build() {
        Song song = new Song(id, duration, beatsPerMinute, notes.toArray(new Note[0]));
        musicManager.register(song);
        return song;
    }
}
