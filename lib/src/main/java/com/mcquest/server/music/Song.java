package com.mcquest.server.music;

import java.time.Duration;

public class Song {
    private final int id;
    private final Duration duration;
    private final Tone[] tones;

    Song(int id, Duration duration, Tone[] tones) {
        this.id = id;
        this.duration = duration;
        this.tones = tones;
    }

    public int getId() {
        return id;
    }

    public Duration getDuration() {
        return duration;
    }

    public int getToneCount() {
        return tones.length;
    }

    public Tone getTone(int index) {
        return tones[index];
    }
}
