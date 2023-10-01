package com.mcquest.server.constants;

import com.mcquest.core.asset.Asset;
import com.mcquest.core.music.Song;
import com.mcquest.server.Assets;

public class Music {
    public static final Song WILDERNESS = new Song(1, audio("wilderness"));
    public static final Song DUNGEON = new Song(2, audio("dungeon"));
    public static final Song BROODMOTHER_LAIR = new Song(3, audio("broodmother_lair"));
    public static final Song BROODMOTHER_BATTLE = new Song(4, audio("broodmother_battle"));

    public static Song[] all() {
        return new Song[]{
                WILDERNESS,
                DUNGEON,
                BROODMOTHER_LAIR,
                BROODMOTHER_BATTLE
        };
    }

    private static Asset audio(String name) {
        String path = "music/" + name + ".ogg";
        return Assets.asset(path);
    }
}
