package com.mcquest.core.character;

import net.minestom.server.entity.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * The CharacterEntityManager provides a standard way to associate Entities
 * with Characters.
 */
public class CharacterEntityManager {
    private final Map<Entity, Character> bindings = new HashMap<>();

    /**
     * Associates the given Entity with the given Character.
     */
    public void bind(Entity entity, Character character) {
        if (bindings.containsKey(entity)) {
            throw new IllegalArgumentException("entity already bound");
        }
        bindings.put(entity, character);
    }

    /**
     * Unassociates the given entity with a previously-associated Character.
     */
    public void unbind(Entity entity) {
        if (!bindings.containsKey(entity)) {
            throw new IllegalArgumentException("entity not bound");
        }
        bindings.remove(entity);
    }

    /**
     * Returns the Character associated with the given Entity, or null if none
     * exists.
     */
    public Character getCharacter(Entity entity) {
        return bindings.get(entity);
    }
}
