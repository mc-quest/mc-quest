package net.mcquest.server.constants;

import net.mcquest.core.instance.Instance;
import net.minestom.server.instance.AnvilLoader;

public class Instances {
    public static final Instance ELADRADOR = Instance.builder("eladrador")
            .chunkLoader(new AnvilLoader("world/eladrador"))
            .build();
    public static final Instance BROODMOTHER_LAIR = Instance.builder("broodmother_lair")
            .chunkLoader(new AnvilLoader("world/broodmother_lair"))
            .build();
    public static final Instance SKULL_ENTRANCE = Instance.builder("skull_entrance")
            .chunkLoader(new AnvilLoader("world/skull_entrance"))
            .build();
    public static final Instance VAMPIRE_CASTLE = Instance.builder("vampire_castle")
            .chunkLoader(new AnvilLoader("world/vampire_castle"))
            .build();

    public static Instance[] all() {
        return new Instance[]{
                ELADRADOR,
                BROODMOTHER_LAIR,
                SKULL_ENTRANCE,
                VAMPIRE_CASTLE
        };
    }
}
