package com.mcquest.core.object;

import com.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public class Object {
    private Instance instance;
    private Pos position;
    private Vec boundingBox;
    private boolean spawned;
    private boolean removed;
    private ObjectManager objectManager;

    public Object(Instance instance, Pos position) {
        this.instance = instance;
        this.position = position;
        this.boundingBox = Vec.ZERO;
        spawned = false;
        removed = false;
    }

    public final Instance getInstance() {
        return instance;
    }

    @MustBeInvokedByOverriders
    public void setInstance(Instance instance, Pos position) {
        Instance oldInstance = this.instance;
        Pos oldPosition = this.position;

        this.instance = instance;
        this.position = position;

        if (objectManager != null) {
            objectManager.updateInstance(this,
                    oldInstance, oldPosition,
                    instance, position);
        }
    }

    public final Pos getPosition() {
        return position;
    }

    @MustBeInvokedByOverriders
    public void setPosition(Pos position) {
        Pos oldPosition = this.position;

        this.position = position;

        if (objectManager != null) {
            objectManager.updatePosition(this, oldPosition, position);
        }
    }

    public final Vec getBoundingBox() {
        return boundingBox;
    }

    public final void setBoundingBox(Vec boundingBox) {
        Vec oldBoundingBox = this.boundingBox;

        this.boundingBox = boundingBox;

        if (objectManager != null) {
            objectManager.updateBoundingBox(this, oldBoundingBox, boundingBox);
        }
    }

    public boolean isSpawned() {
        return spawned;
    }

    @MustBeInvokedByOverriders
    protected void spawn() {
        spawned = true;
    }

    @MustBeInvokedByOverriders
    protected void despawn() {
        spawned = false;
    }

    public boolean isRemoved() {
        return removed;
    }

    public final void remove() {
        if (removed) {
            return;
        }

        if (isSpawned()) {
            despawn();
        }

        if (objectManager != null) {
            objectManager.remove(this);
            objectManager = null;
        }

        removed = true;
    }

    void setObjectManager(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }
}
