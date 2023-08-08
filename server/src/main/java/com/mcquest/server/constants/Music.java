package com.mcquest.server.constants;

import com.mcquest.core.asset.Asset;
import com.mcquest.core.music.Song;
import com.mcquest.server.Assets;

public class Music {
    public static final Song WILDERNESS = new Song(1, audio("wilderness"));
    public static final Song DUNGEON = new Song(2, audio("dungeon"));

    public static Song[] all() {
        return new Song[]{
                WILDERNESS,
                DUNGEON
        };
    }

    private static Asset audio(String name) {
        String path = "music/" + name + ".ogg";
        return Assets.asset(path);
    }
}
