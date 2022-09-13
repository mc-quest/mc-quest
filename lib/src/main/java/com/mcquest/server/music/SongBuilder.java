package com.mcquest.server.music;

import net.minestom.server.sound.SoundEvent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class SongBuilder {
    private final MusicManager musicManager;
    private final int id;
    private final double duration;
    private final List<Note> notes;

    SongBuilder(MusicManager musicManager, int id, double duration) {
        this.musicManager = musicManager;
        this.id = id;
        this.duration = duration;
        this.notes = new ArrayList<>();
    }

    public void note(Duration time, SoundEvent timbre, float volume, float pitch) {
        notes.add(new Note(time, timbre, volume, pitch));
    }

    public Song build() {
        Song song = new Song(id, duration, notes.toArray(new Note[0]));
        musicManager.register(song);
        return song;
    }
}
