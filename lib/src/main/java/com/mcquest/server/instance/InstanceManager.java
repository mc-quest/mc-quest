package com.mcquest.server.instance;

import net.minestom.server.instance.Instance;

import java.util.HashMap;
import java.util.Map;

/**
 * The InstanceManager provides a standard way to register Instances under a
 * name. This enables persistently storing a PlayerCharacter's Instance. Any
 * Instance that a PlayerCharacter can visit should be registered with the
 * InstanceManager.
 */
public class InstanceManager {
    private static final Map<String, Instance> instances = new HashMap<>();

    public static void register(String name, Instance instance) {
        if (instances.containsKey(name)) {
            throw new IllegalArgumentException("instance already registered");
        }
        instances.put(name, instance);
    }

    public static Instance getInstance(String name) {
        return instances.get(name);
    }
}
