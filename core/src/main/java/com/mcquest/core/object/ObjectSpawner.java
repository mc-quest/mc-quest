package com.mcquest.core.object;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.Nullable;

public final class ObjectSpawner {
    private final Instance instance;
    private final Pos position;
    private final ObjectProvider objectProvider;
    private @Nullable Object object;
    private ObjectManager objectManager;

    public ObjectSpawner(Instance instance, Pos position, ObjectProvider objectProvider) {
        this.instance = instance;
        this.position = position;
        this.objectProvider = objectProvider;
        object = null;
        objectManager = null;
    }

    public Instance getInstance() {
        return instance;
    }

    public Pos getPosition() {
        return position;
    }

    public @Nullable Object getObject() {
        return object;
    }

    public boolean isSpawned() {
        return object != null;
    }

    public void remove() {
        if (objectManager != null) {
            objectManager.removeFromHash(this);
        }
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
