package com.mcquest.core.object;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public abstract class Object {
    private final Mmorpg mmorpg;
    private final ObjectSpawner spawner;
    private Instance instance;
    private Pos position;
    private boolean removed;

    public Object(Mmorpg mmorpg, ObjectSpawner spawner) {
        this.mmorpg = mmorpg;
        this.spawner = spawner;
        this.instance = spawner.getInstance();
        this.position = spawner.getPosition();
        removed = false;
    }

    public final Mmorpg getMmorpg() {
        return mmorpg;
    }

    public final ObjectSpawner getSpawner() {
        return spawner;
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

        if (!removed) {
            mmorpg.getObjectManager().updateInstance(this,
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

        if (!removed) {
            mmorpg.getObjectManager().updatePosition(this, oldPosition, position);
        }
    }

    public final boolean isRemoved() {
        return removed;
    }

    public final void remove() {
        if (removed) {
            return;
        }

        despawn();
        mmorpg.getObjectManager().removeFromHash(this);
        getSpawner().disownObject();
        removed = true;
    }

    protected abstract void spawn();

    protected abstract void despawn();
}
