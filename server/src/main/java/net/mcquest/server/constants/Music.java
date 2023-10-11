package net.mcquest.server.constants;

import net.mcquest.core.asset.Asset;
import net.mcquest.core.music.Song;
import net.mcquest.server.Assets;

public class Music {
    public static final Song WILDERNESS = new Song(1, audio("wilderness"));
    public static final Song DUNGEON = new Song(2, audio("dungeon"));
    public static final Song BROODMOTHER_LAIR = new Song(3, audio("broodmother_lair"));
    public static final Song BROODMOTHER_BATTLE = new Song(4, audio("broodmother_battle"));
    public static final Song CREEPY = new Song(5, audio("creepy"));
    public static final Song CREEPY_2 = new Song(6, audio("creepy_2"));
    public static final Song VILLAGE = new Song(7, audio("village"));

    public static Song[] all() {
        return new Song[]{
                WILDERNESS,
                DUNGEON,
                BROODMOTHER_LAIR,
                BROODMOTHER_BATTLE,
                CREEPY,
                CREEPY_2,
                VILLAGE
        };
    }

    private static Asset audio(String name) {
        String path = "music/" + name + ".ogg";
        return Assets.asset(path);
    }
}
