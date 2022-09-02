package com.mcquest.server.physics;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PhysicsManager {
    /**
     * The lengths, widths, and heights of collider buckets.
     */
    static final int BUCKET_SIZE = 256;

    final Map<ColliderBucketAddress, Set<Collider>> colliderBuckets;

    @ApiStatus.Internal
    public PhysicsManager() {
        colliderBuckets = new HashMap<>();
    }

    public void addCollider(Collider collider) {
        if (collider.isEnabled()) {
            throw new IllegalArgumentException("collider already added");
        }
        collider.enable(this);
    }

    public void removeCollider(Collider collider) {
        if (!collider.isEnabled()) {
            throw new IllegalArgumentException("collider not added");
        }
        collider.disable();
    }

    public RaycastResult raycast(Pos origin, Vec ray, double maxDistance) {
        // TODO
        return null;
    }
}
