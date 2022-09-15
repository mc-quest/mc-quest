package com.mcquest.server.music;

public class Song {
    private final int id;
    private final double duration;
    private final double beatsPerMinute;
    private final Note[] notes;

    Song(int id, double duration, double beatsPerMinute, Note[] notes) {
        this.id = id;
        this.duration = duration;
        this.beatsPerMinute = beatsPerMinute;
        this.notes = notes;
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
}
