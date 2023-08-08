package com.mcquest.core.physics;

import com.mcquest.core.character.Character;
import com.mcquest.core.character.CharacterHitbox;
import com.mcquest.core.character.PlayerCharacter;

import java.util.function.Consumer;

/**
 * Useful for wrapping triggers in collision handlers.
 */
public class Triggers {
    public static Consumer<Collider> character(Consumer<Character> onTrigger) {
        return collider -> {
            if (collider instanceof CharacterHitbox hitbox) {
                onTrigger.accept(hitbox.getCharacter());
            }
        };
    }

    public static Consumer<Collider> playerCharacter(Consumer<PlayerCharacter> onTrigger) {
        return collider -> {
            if (collider instanceof PlayerCharacter.Hitbox hitbox) {
                onTrigger.accept(hitbox.getCharacter());
            }
        };
    }
}
