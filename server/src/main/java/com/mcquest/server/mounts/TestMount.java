package com.mcquest.server.mounts;

import com.mcquest.server.mount.Mount;
import com.mcquest.server.mount.MountType;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;

public class TestMount extends Mount {
    public TestMount() {
        super(1, MountType.GROUND);
    }

    @Override
    protected Entity createEntity() {
        return new Entity(EntityType.HORSE);
    }
}
