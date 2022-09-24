package com.mcquest.server.physics;

import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A collider is an axis-aligned bounding box that can be used to detect
 * collisions.
 */
public class Collider {
    private Instance instance;
    private double minX, minY, minZ, maxX, maxY, maxZ;
    private final Set<ColliderBucketAddress> occupiedBuckets;
    private final Set<Collider> contacts;
    private PhysicsManager physicsManager;

    public Collider(@NotNull Instance instance, double minX, double minY,
                    double minZ, double maxX, double maxY, double maxZ) {
        if (minX > maxX) throw new IllegalArgumentException("minX > maxX");
        if (minY > maxY) throw new IllegalArgumentException("minY > maxY");
        if (minZ > maxZ) throw new IllegalArgumentException("minZ > maxZ");

        this.instance = instance;
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        occupiedBuckets = new HashSet<>();
        contacts = new HashSet<>();
        this.physicsManager = null;
    }

    public Collider(@NotNull Instance instance, @NotNull Pos center,
                    double sizeX, double sizeY, double sizeZ) {
        this(instance, center.x() - sizeX / 2.0, center.y() - sizeY / 2.0,
                center.z() - sizeZ / 2.0, center.x() + sizeX / 2.0,
                center.y() + sizeY / 2.0, center.z() + sizeZ / 2.0);
    }

    public final Instance getInstance() {
        return instance;
    }

    public final void setInstance(@NotNull Instance instance) {
        this.instance = instance;
        handleChange();
    }

    public final double getMinX() {
        return minX;
    }

    public final void setMinX(double minX) {
        this.minX = minX;
        handleChange();
    }

    public final double getMinY() {
        return minY;
    }

    public final void setMinY(double minY) {
        this.minY = minY;
        handleChange();
    }

    public final double getMinZ() {
        return minZ;
    }

    public Pos getMin() {
        return new Pos(minX, minY, minZ);
    }

    public void setMin(Pos min) {
        this.minX = min.x();
        this.minY = min.y();
        this.minZ = min.z();
        handleChange();
    }

    public final void setMinZ(double minZ) {
        this.minZ = minZ;
        handleChange();
    }

    public final double getMaxX() {
        return maxX;
    }

    public final void setMaxX(double maxX) {
        this.maxX = maxX;
        handleChange();
    }

    public final double getMaxY() {
        return maxY;
    }

    public final void setMaxY(double maxY) {
        this.maxY = maxY;
        handleChange();
    }

    public final double getMaxZ() {
        return maxZ;
    }

    public final void setMaxZ(double maxZ) {
        this.maxZ = maxZ;
        handleChange();
    }

    public Pos getMax() {
        return new Pos(maxX, maxY, maxZ);
    }

    public void setMax(Pos max) {
        this.maxX = max.x();
        this.maxY = max.y();
        this.maxZ = max.z();
        handleChange();
    }

    public final double getSizeX() {
        return maxX - minX;
    }

    /**
     * Sets the length of this Collider in the x-direction while preserving
     * its center and lengths on the other axes.
     */
    public final void setSizeX(double sizeX) {
        double centerX = (maxX - minX) / 2.0;
        double semiSizeX = sizeX / 2.0;
        minX = centerX - semiSizeX;
        maxX = centerX + semiSizeX;
        handleChange();
    }

    public final double getSizeY() {
        return maxY - minY;
    }

    /**
     * Sets the length of this Collider in the y-direction while preserving
     * its center and lengths on the other axes.
     */
    public final void setSizeY(double sizeY) {
        double centerY = (maxY - minY) / 2.0;
        double semiSizeY = sizeY / 2.0;
        minY = centerY - semiSizeY;
        maxY = centerY + semiSizeY;
        handleChange();
    }

    public final double getSizeZ() {
        return maxZ - minZ;
    }

    /**
     * Sets the length of this Collider in the z-direction while preserving
     * its center and lengths on the other axes.
     */
    public final void setSizeZ(double sizeZ) {
        double centerZ = (maxZ - minZ) / 2.0;
        double semiSizeZ = sizeZ / 2.0;
        minY = centerZ - semiSizeZ;
        maxY = centerZ + semiSizeZ;
        handleChange();
    }

    /**
     * Returns the center position of this Collider.
     */
    public final Pos getCenter() {
        return new Pos((minX + maxX) / 2.0,
                (minY + maxY) / 2.0,
                (minZ + maxZ) / 2.0);
    }

    public final void setCenter(@NotNull Pos center) {
        double semiSizeX = getSizeX() / 2.0;
        double semiSizeY = getSizeY() / 2.0;
        double semiSizeZ = getSizeZ() / 2.0;
        this.minX = center.x() - semiSizeX;
        this.minY = center.y() - semiSizeY;
        this.minZ = center.z() - semiSizeZ;
        this.maxX = center.x() + semiSizeX;
        this.maxY = center.y() + semiSizeY;
        this.maxZ = center.z() + semiSizeZ;
        handleChange();
    }

    boolean isEnabled() {
        return physicsManager != null;
    }

    void enable(PhysicsManager physicsManager) {
        this.physicsManager = physicsManager;
        updateOccupiedBuckets();
        checkForCollisions();
    }

    void disable() {
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
            this.onCollisionExit(other);
            other.onCollisionExit(this);
        }

        this.physicsManager = null;
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
        int minBucketX = (int) Math.floor(minX / PhysicsManager.COLLIDER_BUCKET_SIZE);
        int minBucketY = (int) Math.floor(minY / PhysicsManager.COLLIDER_BUCKET_SIZE);
        int minBucketZ = (int) Math.floor(minZ / PhysicsManager.COLLIDER_BUCKET_SIZE);
        int maxBucketX = (int) Math.floor(maxX / PhysicsManager.COLLIDER_BUCKET_SIZE);
        int maxBucketY = (int) Math.floor(maxY / PhysicsManager.COLLIDER_BUCKET_SIZE);
        int maxBucketZ = (int) Math.floor(maxZ / PhysicsManager.COLLIDER_BUCKET_SIZE);
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
            this.onCollisionEnter(other);
            other.onCollisionEnter(this);
        }
        for (Collider other : exitingColliders) {
            this.onCollisionExit(other);
            other.onCollisionExit(this);
        }
    }

    private boolean overlaps(Collider other) {
        return this.instance == other.instance &&
                this.minX <= other.maxX && this.minY <= other.maxY &&
                this.minZ <= other.maxZ && this.maxX >= other.minX &&
                this.maxY >= other.minY && this.maxZ >= other.minZ;
    }

    /**
     * Invoked when this Collider enters a collision with another Collider.
     */
    protected void onCollisionEnter(Collider other) {
    }

    /**
     * Invoked when this Collider exits a collision with another Collider.
     */
    protected void onCollisionExit(Collider other) {
    }
}
