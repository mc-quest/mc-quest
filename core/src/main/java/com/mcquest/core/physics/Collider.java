package com.mcquest.core.physics;

import com.mcquest.core.instance.Instance;
import com.mcquest.core.object.SpatialHashCell;
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
    private final Set<SpatialHashCell> occupiedCells;
    private final Set<Collider> contacts;
    private PhysicsManager physicsManager;
    private boolean removed;

    public Collider(@NotNull Instance instance, @NotNull Pos min, @NotNull Pos max) {
        validateBounds(min, max);
        this.instance = instance;
        this.min = min;
        this.max = max;
        onCollisionEnter = null;
        onCollisionExit = null;
        occupiedCells = new HashSet<>();
        contacts = new HashSet<>();
        physicsManager = null;
        removed = false;
    }

    public Collider(@NotNull Instance instance, @NotNull Pos center, @NotNull Vec extents) {
        this(instance, center.sub(extents.mul(0.5)), center.add(extents.mul(0.5)));
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

    public final @NotNull Pos getCenter() {
        return min.add(max).mul(0.5);
    }

    public final void setCenter(@NotNull Pos center) {
        Vec halfExtents = getExtents().mul(0.5);
        Pos min = center.sub(halfExtents);
        Pos max = center.add(halfExtents);
        validateBounds(min, max);
        this.min = min;
        this.max = max;
        handleChange();
    }

    public final @NotNull Vec getExtents() {
        return max.sub(min).asVec();
    }

    public final void setExtents(@NotNull Vec extents) {
        Pos center = getCenter();
        Vec halfExtents = extents.mul(0.5);
        Pos min = center.sub(halfExtents);
        Pos max = center.add(halfExtents);
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

    public final boolean isRemoved() {
        return removed;
    }

    public final void remove() {
        if (removed) {
            return;
        }

        if (physicsManager != null) {
            for (SpatialHashCell cell : occupiedCells) {
                physicsManager.colliders.remove(cell, this);
            }

            for (Collider other : contacts) {
                handleCollisionExit(other);
                other.contacts.remove(this);
            }
        }

        physicsManager = null;
        removed = true;
    }

    void enable(PhysicsManager physicsManager) {
        if (this.physicsManager != null || removed) {
            throw new IllegalStateException();
        }

        this.physicsManager = physicsManager;
        updateOccupiedCells();
        checkForCollisions();
    }

    private static void validateBounds(Pos min, Pos max) {
        if (!geq(max, min)) throw new IllegalArgumentException("!(max >= min)");
    }

    /**
     * Returns p1 >= p2.
     */
    private static boolean geq(Pos p1, Pos p2) {
        return p1.x() >= p2.x() && p1.y() >= p2.y() && p1.z() >= p2.z();
    }

    private void handleChange() {
        if (physicsManager != null) {
            updateOccupiedCells();
            checkForCollisions();
        }
    }

    private void updateOccupiedCells() {
        // Compute new cells.
        Set<SpatialHashCell> newOccupiedCells = new HashSet<>();

        SpatialHashCell minCell = physicsManager.cell(instance, min);
        SpatialHashCell maxCell = physicsManager.cell(instance, max);

        SpatialHashCell.forAllInRange(minCell, maxCell, cell -> {
            newOccupiedCells.add(cell);
            physicsManager.colliders.put(cell, this);
        });

        // Remove from old cells.
        for (SpatialHashCell oldCell : occupiedCells) {
            if (!newOccupiedCells.contains(oldCell)) {
                physicsManager.colliders.remove(oldCell, this);
            }
        }

        occupiedCells.clear();
        occupiedCells.addAll(newOccupiedCells);
    }

    private void checkForCollisions() {
        Collection<Collider> enteringColliders = new ArrayList<>();
        Collection<Collider> exitingColliders = new ArrayList<>();
        for (SpatialHashCell cell : occupiedCells) {
            for (Collider other : physicsManager.colliders.get(cell)) {
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
        return overlapsBox(other.min, other.max);
    }

    boolean overlapsBox(Pos min, Pos max) {
        return geq(this.max, min) && geq(max, this.min);
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
