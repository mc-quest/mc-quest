package net.mcquest.core.physics;

import net.mcquest.core.character.Character;
import net.mcquest.core.character.CharacterHitbox;
import net.mcquest.core.character.PlayerCharacter;
import net.minestom.server.coordinate.Pos;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
        return character(character -> {
            if (character instanceof PlayerCharacter pc) {
                onTrigger.accept(pc);
            }
        });
    }

    public static Consumer<RaycastHit> character(BiConsumer<Character, Pos> onHit) {
        return raycastHit -> {
            if (raycastHit.getCollider() instanceof CharacterHitbox hitbox) {
                onHit.accept(hitbox.getCharacter(), raycastHit.getPosition());
            }
        };
    }

    public static Predicate<Collider> raycastFilter(Predicate<Character> characterFilter) {
        return collider -> collider instanceof CharacterHitbox hitbox
                && characterFilter.test(hitbox.getCharacter());
    }
}
