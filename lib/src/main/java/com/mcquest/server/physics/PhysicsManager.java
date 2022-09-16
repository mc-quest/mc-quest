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

        int bucketMinX = (int) (min.x() / COLLIDER_BUCKET_SIZE);
        int bucketMinY = (int) (min.y() / COLLIDER_BUCKET_SIZE);
        int bucketMinZ = (int) (min.z() / COLLIDER_BUCKET_SIZE);

        int bucketMaxX = (int) (max.x() / COLLIDER_BUCKET_SIZE);
        int bucketMaxY = (int) (max.y() / COLLIDER_BUCKET_SIZE);
        int bucketMaxZ = (int) (max.z() / COLLIDER_BUCKET_SIZE);

        Set<Collider> nearbyColliders = new HashSet<>();

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

        Collections.sort(hits, new RaycastHitComparator(origin));

        return hits;
    }

    private Pos rayColliderIntersection(Pos origin, Vec direction, double maxDistance, Collider collider) {
        double originX = origin.x();
        double originY = origin.y();
        double originZ = origin.z();

        double directionX = direction.x();
        double directionY = direction.y();
        double directionZ = direction.z();

        double divX = 1.0 / directionX;
        double divY = 1.0 / directionY;
        double divZ = 1.0 / directionZ;

        double colliderMinX = collider.getMinX();
        double colliderMinY = collider.getMinY();
        double colliderMinZ = collider.getMinZ();
        double colliderMaxX = collider.getMaxX();
        double colliderMaxY = collider.getMaxY();
        double colliderMaxZ = collider.getMaxZ();

        double tMin;
        double tMax;

        // Intersections with x planes:
        if (directionX >= 0.0) {
            tMin = (colliderMinX - originX) * divX;
            tMax = (colliderMaxX - originX) * divX;
        } else {
            tMin = (colliderMaxX - originX) * divX;
            tMax = (colliderMinX - originX) * divX;
        }

        // Intersections with y planes:
        double tyMin;
        double tyMax;
        if (directionY >= 0.0) {
            tyMin = (colliderMinY - originY) * divY;
            tyMax = (colliderMaxY - originY) * divY;
        } else {
            tyMin = (colliderMaxY - originY) * divY;
            tyMax = (colliderMinY - originY) * divY;
        }
        if ((tMin > tyMax) || (tMax < tyMin)) {
            return null;
        }
        if (tyMin > tMin) {
            tMin = tyMin;
        }
        if (tyMax < tMax) {
            tMax = tyMax;
        }

        // Intersections with z planes:
        double tzMin;
        double tzMax;
        if (directionZ >= 0.0) {
            tzMin = (colliderMinZ - originZ) * divZ;
            tzMax = (colliderMaxZ - originZ) * divZ;
        } else {
            tzMin = (colliderMaxZ - originZ) * divZ;
            tzMax = (colliderMinZ - originZ) * divZ;
        }
        if ((tMin > tzMax) || (tMax < tzMin)) {
            return null;
        }
        if (tzMin > tMin) {
            tMin = tzMin;
        }
        if (tzMax < tMax) {
            tMax = tzMax;
        }

        // Intersections are behind the origin:
        if (tMax < 0.0) return null;

        // Intersections are too far away:
        if (tMin > maxDistance) {
            return null;
        }

        // Find the closest intersection:
        double t;
        if (tMin < 0.0) {
            t = tMax;
        } else {
            t = tMin;
        }

        return origin.add(direction.mul(t));
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
