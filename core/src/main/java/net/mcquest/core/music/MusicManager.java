package net.mcquest.core.music;

import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MusicManager {
    private final Map<Integer, Song> musicById;

    @ApiStatus.Internal
    public MusicManager(Song[] music) {
        musicById = new HashMap<>();
        for (Song song : music) {
            registerSong(song);
        }
    }

    private void registerSong(Song song) {
        int id = song.getId();
        if (musicById.containsKey(id)) {
            throw new IllegalArgumentException("ID already in use: " + id);
        }
        musicById.put(id, song);
    }

    public Song getSong(int id) {
        return musicById.get(id);
    }

    public Collection<Song> getMusic() {
        return Collections.unmodifiableCollection(musicById.values());
    }
}
