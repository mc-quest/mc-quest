package com.mcquest.server.constants;

import com.mcquest.server.audio.Song;

public class Music {
    public static final Song DUNGEON = new Song(1, AudioClips.PORTCULLIS_CLOSE);

    public static Song[] all() {
        return new Song[]{
                DUNGEON
        };
    }
}
