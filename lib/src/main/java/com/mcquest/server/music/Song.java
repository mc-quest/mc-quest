package com.mcquest.server.music;

import net.minestom.server.sound.SoundEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Song {
    private final int id;
    private final double duration;
    private final double beatsPerMinute;
    private final Note[] notes;

    private Song(Builder builder) {
        this.id = builder.id;
        this.duration = builder.duration;
        this.beatsPerMinute = builder.beatsPerMinute;
        this.notes = builder.notes.toArray(new Note[0]);
    }

    public int getId() {
        return id;
    }

    public double getDuration() {
        return duration;
    }

    public double getBeatsPerMinute() {
        return beatsPerMinute;
    }

    public int getNoteCount() {
        return notes.length;
    }

    public Note getNote(int index) {
        return notes[index];
    }

    public static Builder builder(int id, double duration, double beatsPerMinute) {
        return new Builder(id, duration, beatsPerMinute);
    }

    public static class Builder {
        private final int id;
        private final double duration;
        private final double beatsPerMinute;
        private final List<Note> notes;
        private SoundEvent instrument;
        private float volume;

        private Builder(int id, double duration, double beatsPerMinute) {
            this.id = id;
            this.duration = duration;
            this.beatsPerMinute = beatsPerMinute;
            this.notes = new ArrayList<>();
            this.instrument = null;
            this.volume = 1f;
        }

        public Builder instrument(@NotNull SoundEvent instrument) {
            this.instrument = instrument;
            return this;
        }

        public Builder volume(float volume) {
            this.volume = volume;
            return this;
        }

        public Builder note(double time, Pitch pitch) {
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
            return new Song(this);
        }
    }
}
