package net.mcquest.server.constants;

import net.mcquest.core.asset.Asset;
import net.mcquest.core.music.Song;
import net.mcquest.server.Assets;

public class Music {
    public static final Song WILDERNESS = new Song("wilderness", audio("wilderness"));
    public static final Song DUNGEON = new Song("dungeon", audio("dungeon"));
    public static final Song BROODMOTHER_LAIR = new Song("broodmother_lair", audio("broodmother_lair"));
    public static final Song BROODMOTHER_BATTLE = new Song("broodmother_battle", audio("broodmother_battle"));
    public static final Song CREEPY = new Song("creepy", audio("creepy"));
    public static final Song CREEPY_2 = new Song("creepy_2", audio("creepy_2"));
    public static final Song VILLAGE = new Song("village", audio("village"));
    public static final Song WOLF_DEN = new Song("wolf_den", audio("wolf_den"));
    public static final Song ALPHA_WOLF_BATTLE = new Song("alpha_wolf_battle", audio("alpha_wolf_battle"));

    public static Song[] all() {
        return new Song[]{
                WILDERNESS,
                DUNGEON,
                BROODMOTHER_LAIR,
                BROODMOTHER_BATTLE,
                CREEPY,
                CREEPY_2,
                VILLAGE,
                WOLF_DEN,
                ALPHA_WOLF_BATTLE
        };
    }

    private static Asset audio(String name) {
        String path = "music/" + name + ".ogg";
        return Assets.asset(path);
    }
}
