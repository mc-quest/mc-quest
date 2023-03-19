package com.mcquest.server.physics;

import com.mcquest.server.instance.Instance;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;

/**
 * A collider is an axis-aligned bounding box that can be used to detect
 * collisions.
 */
public class Collider {
    private @NotNull Instance instance;
    private @NotNull Pos min, max;
    private Consumer<Collider> onCollisionEnter;
    private Consumer<Collider> onCollisionExit;
    private final Set<ColliderBucketAddress> occupiedBuckets;
    private final Set<Collider> contacts;
    private PhysicsManager physicsManager;

    public Collider(@NotNull Instance instance, @NotNull Pos min, @NotNull Pos max) {
        validateBounds(min, max);
        this.instance = instance;
        this.min = min;
        this.max = max;
        onCollisionEnter = null;
        onCollisionExit = null;
        occupiedBuckets = new HashSet<>();
        contacts = new HashSet<>();
        physicsManager = null;
    }

    public Collider(@NotNull Instance instance, @NotNull Pos center, @NotNull Vec size) {
        this(instance, center.sub(size.mul(0.5)), center.add(size.mul(0.5)));
    }

    public final @NotNull Instance getInstance() {
        return instance;
    }

    public final void setInstance(@NotNull Instance instance) {
        this.instance = instance;
        handleChange();
    }

    public final @NotNull Pos getMin() {
        return min;
    }

    public final void setMin(@NotNull Pos min) {
        validateBounds(min, this.max);
        this.min = min;
        handleChange();
    }

    public final @NotNull Pos getMax() {
        return max;
    }

    public final void setMax(@NotNull Pos max) {
        validateBounds(this.min, max);
        this.max = max;
        handleChange();
    }

    /**
     * Returns the center position of this Collider.
     */
    public final @NotNull Pos getCenter() {
        return min.add(max).mul(0.5);
    }

    public final void setCenter(@NotNull Pos center) {
        Vec semiSize = getSize().mul(0.5);
        Pos min = center.sub(semiSize);
        Pos max = center.add(semiSize);
        validateBounds(min, max);
        this.min = min;
        this.max = max;
        handleChange();
    }

    public final @NotNull Vec getSize() {
        return max.sub(min).asVec();
    }

    public final void setSize(@NotNull Vec size) {
        Pos center = getCenter();
        Vec semiSize = size.mul(0.5);
        Pos min = center.sub(semiSize);
        Pos max = center.add(semiSize);
        validateBounds(min, max);
        this.min = min;
        this.max = max;
        handleChange();
    }

    public final void onCollisionEnter(@Nullable Consumer<Collider> onCollisionEnter) {
        this.onCollisionEnter = onCollisionEnter;
    }

    public final void onCollisionExit(@Nullable Consumer<Collider> onCollisionExit) {
        this.onCollisionExit = onCollisionExit;
    }

    void enable(PhysicsManager physicsManager) {
        if (this.physicsManager == physicsManager) {
            return;
        }

        if (this.physicsManager != null) {
            disable();
        }

        this.physicsManager = physicsManager;
        updateOccupiedBuckets();
        checkForCollisions();
    }

    void disable() {
        if (physicsManager == null) {
            // Already disabled.
            return;
        }

        for (ColliderBucketAddress bucketAddress : occupiedBuckets) {
            removeFromBucket(bucketAddress);
        }
        occupiedBuckets.clear();

        Set<Collider> oldContacts = new HashSet<>(contacts);
        contacts.clear();
        for (Collider other : oldContacts) {
            other.contacts.remove(this);
        }

        for (Collider other : oldContacts) {
            handleCollisionExit(other);
        }

        this.physicsManager = null;
    }

    private void validateBounds(Pos min, Pos max) {
        if (!geq(max, min)) throw new IllegalArgumentException("min > max");
    }

    /**
     * Returns p1 >= p2.
     */
    private boolean geq(Pos p1, Pos p2) {
        return p1.x() >= p2.x() && p1.y() >= p2.y() && p1.z() >= p2.z();
    }

    private void handleChange() {
        if (physicsManager != null) {
            updateOccupiedBuckets();
            checkForCollisions();
        }
    }

    private void updateOccupiedBuckets() {
        // Compute new buckets.
        Set<ColliderBucketAddress> newOccupiedBuckets = new HashSet<>();
        int minBucketX = (int) Math.floor(min.x() / PhysicsManager.COLLIDER_BUCKET_SIZE);
        int minBucketY = (int) Math.floor(min.y() / PhysicsManager.COLLIDER_BUCKET_SIZE);
        int minBucketZ = (int) Math.floor(min.z() / PhysicsManager.COLLIDER_BUCKET_SIZE);
        int maxBucketX = (int) Math.floor(max.x() / PhysicsManager.COLLIDER_BUCKET_SIZE);
        int maxBucketY = (int) Math.floor(max.y() / PhysicsManager.COLLIDER_BUCKET_SIZE);
        int maxBucketZ = (int) Math.floor(max.z() / PhysicsManager.COLLIDER_BUCKET_SIZE);
        for (int x = minBucketX; x <= maxBucketX; x++) {
            for (int y = minBucketY; y <= maxBucketY; y++) {
                for (int z = minBucketZ; z <= maxBucketZ; z++) {
                    ColliderBucketAddress address = new ColliderBucketAddress(instance, x, y, z);
                    newOccupiedBuckets.add(address);
                    if (!physicsManager.colliderBuckets.containsKey(address)) {
                        physicsManager.colliderBuckets.put(address, new HashSet<>());
                    }
                    Set<Collider> bucket = physicsManager.colliderBuckets.get(address);
                    // Redundant adding is fine.
                    bucket.add(this);
                }
            }
        }

        // Remove from old buckets.
        for (ColliderBucketAddress oldAddress : occupiedBuckets) {
            if (!newOccupiedBuckets.contains(oldAddress)) {
                removeFromBucket(oldAddress);
            }
        }

        occupiedBuckets.clear();
        occupiedBuckets.addAll(newOccupiedBuckets);
    }

    private void removeFromBucket(ColliderBucketAddress address) {
        Set<Collider> bucket = physicsManager.colliderBuckets.get(address);
        bucket.remove(this);
        if (bucket.isEmpty()) {
            physicsManager.colliderBuckets.remove(address);
        }
    }

    private void checkForCollisions() {
        Set<Collider> enteringColliders = new HashSet<>();
        Set<Collider> exitingColliders = new HashSet<>();
        for (ColliderBucketAddress bucketAddress : occupiedBuckets) {
            Set<Collider> bucket = new HashSet<>(physicsManager.colliderBuckets.get(bucketAddress));
            for (Collider other : bucket) {
                if (this == other) {
                    continue;
                }
                boolean collides = this.overlaps(other);
                if (contacts.contains(other)) {
                    if (!collides) {
                        exitingColliders.add(other);
                        this.contacts.remove(other);
                        other.contacts.remove(this);
                    }
                } else {
                    if (collides) {
                        this.contacts.add(other);
                        other.contacts.add(this);
                        enteringColliders.add(other);
                    }
                }
            }
        }

        for (Collider other : enteringColliders) {
            handleCollisionEnter(other);
        }
        for (Collider other : exitingColliders) {
            handleCollisionExit(other);
        }
    }

    private boolean overlaps(Collider other) {
        return this.instance == other.instance &&
                geq(this.max, other.min) &&
                geq(other.max, this.min);
    }

    private void handleCollisionEnter(Collider other) {
        if (this.onCollisionEnter != null)
            this.onCollisionEnter.accept(other);
        if (other.onCollisionEnter != null)
            other.onCollisionEnter.accept(this);
    }

    private void handleCollisionExit(Collider other) {
        if (this.onCollisionExit != null)
            this.onCollisionExit.accept(other);
        if (other.onCollisionExit != null)
            other.onCollisionExit.accept(this);
    }
}
