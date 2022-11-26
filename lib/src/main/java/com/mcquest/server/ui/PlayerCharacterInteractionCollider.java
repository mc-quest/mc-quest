package com.mcquest.server.ui;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.instance.Instance;
import com.mcquest.server.physics.Collider;
import net.minestom.server.coordinate.Pos;

import java.util.function.Consumer;

public final class PlayerCharacterInteractionCollider extends Collider {
    private final Consumer<PlayerCharacter> onInteract;

    public PlayerCharacterInteractionCollider(Instance instance, Pos center,
                                              double lengthX, double lengthY, double lengthZ,
                                              Consumer<PlayerCharacter> onInteract) {
        super(instance, center, lengthX, lengthY, lengthZ);
        this.onInteract = onInteract;
    }

    public void interact(PlayerCharacter pc) {
        onInteract.accept(pc);
    }
}
