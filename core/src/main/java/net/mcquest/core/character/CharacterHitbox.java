package net.mcquest.core.character;

import net.mcquest.core.instance.Instance;
import net.mcquest.core.physics.Collider;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

public final class CharacterHitbox extends Collider {
    private final Character character;

    CharacterHitbox(Character character, Instance instance, Pos center, Vec extents) {
        super(instance, center, extents);
        this.character = character;
    }

    public Character getCharacter() {
        return character;
    }
}
