package com.mcquest.core.character;

import com.mcquest.core.instance.Instance;
import com.mcquest.core.physics.Collider;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

public class CharacterHitbox extends Collider {
    private final Character character;

    public CharacterHitbox(Character character, Instance instance, Pos center, Vec size) {
        super(instance, center, size);
        this.character = character;
    }

    public Character getCharacter() {
        return character;
    }
}
