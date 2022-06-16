package com.mcquest.server.character;

import net.minestom.server.entity.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * The CharacterEntityManager provides a standard way to associate Entities
 * with Characters.
 */
public class CharacterEntityManager {
    private static final Map<Entity, Character> map = new HashMap<>();

    /**
     * Associates the given Entity with the given Character.
     */
    public static void register(Entity entity, Character character) {
        if (map.containsKey(entity)) {
            throw new IllegalArgumentException("entity already registered");
        }
        map.put(entity, character);
    }

    /**
     * Unassociates the given entity with a previously-associated Character.
     */
    public static void unregister(Entity entity) {
        if (!map.containsKey(entity)) {
            throw new IllegalArgumentException("entity not registered");
        }
        map.remove(entity);
    }

    /**
     * Returns the Character associated with the given Entity, or null if none
     * exists.
     */
    public static Character getCharacter(Entity entity) {
        return map.get(entity);
    }
}
