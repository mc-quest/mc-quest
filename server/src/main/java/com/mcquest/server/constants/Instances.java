package com.mcquest.server.constants;

import com.mcquest.core.instance.Instance;
import net.minestom.server.instance.AnvilLoader;

public class Instances {
    public static final Instance ELADRADOR = Instance.builder(1)
            .chunkLoader(new AnvilLoader("world/eladrador"))
            .build();
    public static final Instance BROODMOTHER_LAIR = Instance.builder(2)
            .chunkLoader(new AnvilLoader("world/broodmother_lair"))
            .build();

    public static Instance[] all() {
        return new Instance[]{
                ELADRADOR,
                BROODMOTHER_LAIR
        };
    }
}
