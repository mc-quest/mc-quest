package com.mcquest.server.constants;

import com.mcquest.core.instance.Instance;
import net.minestom.server.instance.AnvilLoader;

public class Instances {
    public static final Instance ELADRADOR = Instance.builder(1)
            .chunkLoader(new AnvilLoader("world/eladrador"))
            .build();
    public static final Instance BULSKAN_RUINS = Instance.builder(2)
            .chunkLoader(new AnvilLoader("world/bulskan_ruins"))
            .build();

    public static Instance[] all() {
        return new Instance[]{
                ELADRADOR,
                BULSKAN_RUINS
        };
    }
}
