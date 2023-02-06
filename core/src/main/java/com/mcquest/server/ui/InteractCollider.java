package com.mcquest.server.ui;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.instance.Instance;
import com.mcquest.server.physics.Collider;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;

import java.util.function.Consumer;

/**
 * A collider that a player can right-click to interact with.
 */
public final class InteractCollider extends Collider {
    private final Consumer<PlayerCharacter> onInteract;

    public InteractCollider(Instance instance, Pos center, Vec size,
                            Consumer<PlayerCharacter> onInteract) {
        super(instance, center, size);
        this.onInteract = onInteract;
    }

    public void interact(PlayerCharacter pc) {
        onInteract.accept(pc);
    }
}
