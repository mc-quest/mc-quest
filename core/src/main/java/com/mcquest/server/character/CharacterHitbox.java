package com.mcquest.server.character;

import com.mcquest.server.instance.Instance;
import com.mcquest.server.physics.Collider;
import net.minestom.server.coordinate.Pos;

public class CharacterHitbox extends Collider {
    private final Character character;

    public CharacterHitbox(Character character, Instance instance,
                           Pos center, double sizeX, double sizeY,
                           double sizeZ) {
        super(instance, center, sizeX, sizeY, sizeZ);
        this.character = character;
    }

    public Character getCharacter() {
        return character;
    }
}
