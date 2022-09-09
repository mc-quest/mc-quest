package com.mcquest.server.physics;

import com.mcquest.server.util.MathUtility;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class PhysicsManager {
    /**
     * The lengths, widths, and heights of collider buckets.
     */
    static final int COLLIDER_BUCKET_SIZE = 256;

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

    public List<RaycastHit> raycast(Instance instance, Pos origin, Vec direction, double maxDistance) {
        if (!direction.isNormalized()) {
            direction = direction.normalize();
        }
        Pos end = origin.add(direction.mul(maxDistance));
        Pos min = MathUtility.min(origin, end);
        Pos max = MathUtility.max(origin, end);
        List<Collider> nearbyColliders = new ArrayList<>();

        int bucketMinX = (int) (min.x() / COLLIDER_BUCKET_SIZE);
        int bucketMinY = (int) (min.y() / COLLIDER_BUCKET_SIZE);
        int bucketMinZ = (int) (min.z() / COLLIDER_BUCKET_SIZE);

        int bucketMaxX = (int) (max.x() / COLLIDER_BUCKET_SIZE);
        int bucketMaxY = (int) (max.y() / COLLIDER_BUCKET_SIZE);
        int bucketMaxZ = (int) (max.z() / COLLIDER_BUCKET_SIZE);

        for (int x = bucketMinX; x <= bucketMaxX; x++) {
            for (int y = bucketMinY; y <= bucketMaxY; y++) {
                for (int z = bucketMinZ; z <= bucketMaxZ; z++) {
                    ColliderBucketAddress bucketAddress = new ColliderBucketAddress(instance, x, y, z);
                    Set<Collider> bucket = colliderBuckets.get(bucketAddress);
                    if (bucket != null) {
                        nearbyColliders.addAll(bucket);
                    }
                }
            }
        }

        List<RaycastHit> hits = new ArrayList<>();
        for (Collider collider : nearbyColliders) {
            Pos intersection = rayColliderIntersection(origin, direction, maxDistance, collider);
            if (intersection != null) {
                RaycastHit hit = new RaycastHit(collider, intersection);
                hits.add(hit);
            }
        }

        RaycastHitComparator comparator = new RaycastHitComparator(origin);
        Collections.sort(hits, comparator);

        return hits;
    }

    private Pos rayColliderIntersection(Pos origin, Vec direction, double maxDistance, Collider collider) {
        // TODO
        return null;
    }

    private static final class RaycastHitComparator implements Comparator<RaycastHit> {
        private final Pos origin;

        private RaycastHitComparator(Pos origin) {
            this.origin = origin;
        }

        @Override
        public int compare(RaycastHit hit1, RaycastHit hit2) {
            return Double.compare(hit1.getPosition().distanceSquared(origin),
                    hit2.getPosition().distanceSquared(origin));
        }
    }
}
