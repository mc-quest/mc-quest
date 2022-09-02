package com.mcquest.server.world;

import net.minestom.server.instance.Instance;

import java.util.HashMap;
import java.util.Map;

public class WorldManager {
    private final Map<String, Instance> worlds;

    public WorldManager() {
        this.worlds = new HashMap<>();
    }

    public Instance getWorld(String name) {
        return worlds.get(name);
    }

    public Instance registerWorld(String name, Instance instance) {

    }
}
