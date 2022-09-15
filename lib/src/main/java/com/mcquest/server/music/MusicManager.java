package com.mcquest.server.music;

import java.util.HashMap;
import java.util.Map;

public class MusicManager {
    private final Map<Integer, Song> songsById;

    public MusicManager() {
        songsById = new HashMap<>();
    }

    public Song getSong(int id) {
        return songsById.get(id);
    }

    void register(Song song) {
        int id = song.getId();
        if (songsById.containsKey(id)) {
            throw new IllegalArgumentException("Song ID already in use: " + id);
        }
        songsById.put(id, song);
    }

    public SongBuilder songBuilder(int id, double duration, double beatsPerMinute) {
        return new SongBuilder(this, id, duration, beatsPerMinute);
    }
}

