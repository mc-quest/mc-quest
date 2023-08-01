package com.mcquest.core.entity;

import com.mcquest.core.character.Character;
import com.mcquest.core.character.PlayerCharacter;
import net.minestom.server.entity.Entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a standard way to bind entities with characters, enabling the use of
 * TargetSelectors to target characters.
 */
public class CharacterEntityManager {
    private final Map<Entity, Character> bindings = new HashMap<>();

    /**
     * Binds the given entity with the given character.
     */
    public void bind(Entity entity, Character character) {
        if (bindings.containsKey(entity)) {
            throw new IllegalArgumentException("entity already bound");
        }
        bindings.put(entity, character);
    }

    /**
     * Unbinds the given entity with a previously-bound character.
     */
    public void unbind(Entity entity) {
        if (!bindings.containsKey(entity)) {
            throw new IllegalArgumentException("entity not bound");
        }
        bindings.remove(entity);
    }

    /**
     * Returns the character bound to the given entity, or null if none exists.
     */
    public Character getCharacter(Entity entity) {
        Character character = bindings.get(entity);

        if (character instanceof PlayerCharacter pc) {
            if (pc.getCutscenePlayer().getPlayingCutscene() != null) {
                return null;
            }
        }

        return character;
    }
}
