package com.mcquest.server.character;

import com.mcquest.server.instance.Instance;
import com.mcquest.server.physics.Collider;
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
