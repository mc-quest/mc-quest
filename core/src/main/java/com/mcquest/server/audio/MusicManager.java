package com.mcquest.server.audio;

import java.util.HashMap;
import java.util.Map;

public class MusicManager {
    private final Map<Integer, Song> songsById;

    public MusicManager(Song[] songs) {
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

    public Song getSong(int id) {
        return songsById.get(id);
    }
}

