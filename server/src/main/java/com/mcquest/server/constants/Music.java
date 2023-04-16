package com.mcquest.server.constants;

import com.mcquest.server.Assets;
import com.mcquest.server.asset.Asset;
import com.mcquest.server.music.Song;

public class Music {
    public static final Song DUNGEON = new Song(1, audio("Song"));

    public static Song[] all() {
        return new Song[]{
                DUNGEON
        };
    }

    private static Asset audio(String name) {
        String path = "music/" + name + ".ogg";
        return Assets.asset(path);
    }
}
