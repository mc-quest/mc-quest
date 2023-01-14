package com.mcquest.server.constants;

import com.mcquest.server.instance.Instance;
import net.minestom.server.instance.AnvilLoader;

public class Instances {
    public static final Instance ELADRADOR = Instance.builder(1)
            .chunkLoader(new AnvilLoader("world/eladrador"))
            .build();

    public static Instance[] all() {
        return new Instance[]{
                ELADRADOR
        };
    }
}
