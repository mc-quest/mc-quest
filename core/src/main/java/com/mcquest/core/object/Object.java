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
    private ObjectManager objectManager;

    public Object(Instance instance, Pos position) {
        this.instance = instance;
        this.position = position;
        this.boundingBox = Vec.ZERO;
        spawned = false;
    }

    public final Instance getInstance() {
        return instance;
    }

    @MustBeInvokedByOverriders
    public void setInstance(Instance instance) {
        Instance oldInstance = this.instance;

        this.instance = instance;

        if (objectManager != null) {
            objectManager.updateInstance(this, oldInstance, instance);
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

    public final void remove() {
        if (isSpawned()) {
            despawn();
        }

        objectManager.remove(this);
        objectManager = null;
    }
}
