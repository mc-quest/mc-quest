package com.mcquest.core.audio;

import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class AudioManager {
    private final Collection<AudioClip> audioClips;
    private final Map<Integer, Song> songsById;

    @ApiStatus.Internal
    public AudioManager(AudioClip[] audioClips, Song[] songs) {
        this.audioClips = new ArrayList<>();
        this.audioClips.addAll(Arrays.asList(CoreAudio.all()));
        this.audioClips.addAll(Arrays.asList(audioClips));

        songsById = new HashMap<>();
        for (Song song : songs) {
            registerSong(song);
        }
    }

    private void registerSong(Song song) {
        int id = song.getId();
        if (songsById.containsKey(id)) {
            throw new IllegalArgumentException("ID already in use: " + id);
        }
        songsById.put(id, song);
    }

    public Collection<AudioClip> getAudioClips() {
        return Collections.unmodifiableCollection(audioClips);
    }

    public Song getSong(int id) {
        return songsById.get(id);
    }

    public Collection<Song> getSongs() {
        return Collections.unmodifiableCollection(songsById.values());
    }
}

