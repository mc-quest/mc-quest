package com.mcquest.server.constants;

import com.mcquest.core.audio.Song;

public class Music {
    public static final Song WILDERNESS = new Song(1, AudioClips.WILDERNESS);
    public static final Song DUNGEON = new Song(2, AudioClips.DUNGEON);

    public static Song[] all() {
        return new Song[]{
                WILDERNESS,
                DUNGEON
        };
    }
}
