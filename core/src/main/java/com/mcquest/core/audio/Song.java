package com.mcquest.core.audio;

public class Song {
    private final int id;
    private AudioClip audioClip;

    public Song(int id, AudioClip audioClip) {
        this.id = id;
        this.audioClip = audioClip;
    }

    public int getId() {
        return id;
    }

    public AudioClip getAudioClip() {
        return audioClip;
    }
}
