package com.mcquest.core.physics;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.mcquest.core.instance.Instance;
import com.mcquest.core.util.MathUtility;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class PhysicsManager {
    /**
     * The lengths, widths, and heights of collider cells in the spatial hash.
     */
    static final double CELL_SIZE = 256.0;

    final SetMultimap<SpatialHashCell, Collider> colliders;

    @ApiStatus.Internal
    public PhysicsManager() {
        colliders = HashMultimap.create();
    }

    public void addCollider(Collider collider) {
        collider.enable(this);
    }

    public void removeCollider(Collider collider) {
        collider.disable();
    }

    public Collection<RaycastHit> raycastAll(Instance instance, Pos origin,
                                             Vec direction, double maxDistance,
                                             Predicate<Collider> filter) {
        if (!direction.isNormalized()) {
            direction = direction.normalize();
        }

        Pos end = origin.add(direction.mul(maxDistance));
        Pos min = MathUtility.min(origin, end);
        Pos max = MathUtility.max(origin, end);

        SpatialHashCell minCell = SpatialHashCell.cellAt(instance, min, CELL_SIZE);
        SpatialHashCell maxCell = SpatialHashCell.cellAt(instance, max, CELL_SIZE);

        Set<Collider> nearbyColliders = new HashSet<>();
        for (int x = minCell.getX(); x <= maxCell.getX(); x++) {
            for (int y = minCell.getY(); y <= maxCell.getY(); y++) {
                for (int z = minCell.getZ(); z <= maxCell.getZ(); z++) {
                    SpatialHashCell cell = new SpatialHashCell(instance, x, y, z);
                    Set<Collider> cellColliders = colliders.get(cell);
                    for (Collider collider : cellColliders) {
                        if (filter.test(collider)) {
                            nearbyColliders.add(collider);
                        }
                    }
                }
            }
        }

        Collection<RaycastHit> hits = new ArrayList<>();
        for (Collider collider : nearbyColliders) {
            Pos intersection = rayColliderIntersection(origin, direction,
                    maxDistance, collider);
            if (intersection != null) {
                RaycastHit hit = new RaycastHit(collider, intersection);
                hits.add(hit);
            }
        }

        return hits;
    }

    public @Nullable RaycastHit raycast(Instance instance, Pos origin,
                                        Vec direction, double maxDistance,
                                        Predicate<Collider> filter) {
        Collection<RaycastHit> hits = raycastAll(instance, origin, direction,
                maxDistance, filter);

        if (hits.isEmpty()) {
            return null;
        }

        return hits.stream().min((h1, h2) -> {
            double d1 = h1.getPosition().distanceSquared(origin);
            double d2 = h2.getPosition().distanceSquared(origin);
            return Double.compare(d1, d2);
        }).get();
    }

    private Pos rayColliderIntersection(Pos origin, Vec direction,
                                        double maxDistance, Collider collider) {
        double originX = origin.x();
        double originY = origin.y();
        double originZ = origin.z();

        double directionX = direction.x();
        double directionY = direction.y();
        double directionZ = direction.z();

        double divX = directionX == 0.0 ? Double.MAX_VALUE : 1.0 / directionX;
        double divY = directionY == 0.0 ? Double.MAX_VALUE : 1.0 / directionY;
        double divZ = directionZ == 0.0 ? Double.MAX_VALUE : 1.0 / directionZ;

        Pos colliderMin = collider.getMin();
        Pos colliderMax = collider.getMax();
        double colliderMinX = colliderMin.x();
        double colliderMinY = colliderMin.y();
        double colliderMinZ = colliderMin.z();
        double colliderMaxX = colliderMax.x();
        double colliderMaxY = colliderMax.y();
        double colliderMaxZ = colliderMax.z();

        double tMin;
        double tMax;

        // Intersections with x planes.
        if (directionX >= 0.0) {
            tMin = (colliderMinX - originX) * divX;
            tMax = (colliderMaxX - originX) * divX;
        } else {
            tMin = (colliderMaxX - originX) * divX;
            tMax = (colliderMinX - originX) * divX;
        }

        // Intersections with y planes.
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

        // Intersections with z planes.
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

        if (tMax < 0.0) {
            // Intersections are behind the origin.
            return null;
        }

        if (tMin > maxDistance) {
            // Intersections are too far away.
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
}
