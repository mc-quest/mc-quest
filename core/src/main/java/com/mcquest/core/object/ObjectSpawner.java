package com.mcquest.core.object;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.Nullable;

public final class ObjectSpawner {
    private final Instance instance;
    private final Pos position;
    private final ObjectProvider objectProvider;
    private boolean active;
    private @Nullable Object object;
    private ObjectManager objectManager;
    private boolean removed;

    private ObjectSpawner(Instance instance, Pos position, ObjectProvider objectProvider) {
        this.instance = instance;
        this.position = position;
        this.objectProvider = objectProvider;
        active = true;
        object = null;
        objectManager = null;
        removed = false;
    }

    public static ObjectSpawner of(Instance instance, Pos position, ObjectProvider objectProvider) {
        return new ObjectSpawner(instance, position, objectProvider);
    }

    public Instance getInstance() {
        return instance;
    }

    public Pos getPosition() {
        return position;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public @Nullable Object getObject() {
        return object;
    }

    public boolean isSpawned() {
        return object != null;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void remove() {
        if (removed) {
            return;
        }

        if (objectManager != null) {
            objectManager.removeFromHash(this);
        }

        removed = true;
    }

    void setObjectManager(ObjectManager objectManager) {
        this.objectManager = objectManager;
    }

    Object spawn(Mmorpg mmorpg) {
        object = objectProvider.create(mmorpg, this);
        return object;
    }

    void disownObject() {
        object = null;
    }
}
