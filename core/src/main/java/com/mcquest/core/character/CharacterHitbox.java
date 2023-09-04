package com.mcquest.core.character;

import com.mcquest.core.instance.Instance;
import com.mcquest.core.physics.Collider;
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
