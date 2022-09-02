package com.mcquest.server.world;

import net.minestom.server.instance.Instance;

import java.util.HashMap;
import java.util.Map;

/**
 * The InstanceManager provides a standard way to register Instances under a
 * name. This enables persistently storing a PlayerCharacter's Instance. Any
 * Instance that a PlayerCharacter can visit should be registered with the
 * InstanceManager.
 */
public class WorldManager {
    private final Map<String, Instance> instances;

    public WorldManager() {
        this.instances = new HashMap<>();
    }

    public static Instance getInstance(String name) {
        return instances.get(name);
    }
}
