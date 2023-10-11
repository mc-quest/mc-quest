package net.mcquest.core.physics;

import net.minestom.server.coordinate.Pos;

public class RaycastHit {
    private final Collider collider;
    private final Pos position;

    RaycastHit(Collider collider, Pos position) {
        this.collider = collider;
        this.position = position;
    }

    public Collider getCollider() {
        return collider;
    }

    public Pos getPosition() {
        return position;
    }
}
