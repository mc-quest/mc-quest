package com.mcquest.server.constants;

import com.mcquest.server.music.Song;
import com.mcquest.server.util.ResourceUtility;

public class Music {
    public static final Song DUNGEON = new Song(1,
            ResourceUtility.streamSupplier("music/Song.ogg"));

    public static Song[] all() {
        return new Song[]{
                DUNGEON
        };
    }
}
