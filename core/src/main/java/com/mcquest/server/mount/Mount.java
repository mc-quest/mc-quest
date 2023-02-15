package com.mcquest.server.mount;

import net.minestom.server.entity.Entity;
import org.jetbrains.annotations.Nullable;

public abstract class Mount {
    private final int id;
    private final MountType type;

    public Mount(int id, MountType type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public MountType getType() {
        return type;
    }

    protected abstract @Nullable Entity createEntity();
}
