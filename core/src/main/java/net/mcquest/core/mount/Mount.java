package net.mcquest.core.mount;

import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;

import java.util.function.Supplier;

public class Mount {
    private final String id;
    private final MountType type;
    private Supplier<Entity> entity;

    public Mount(String id, MountType type) {
        this.id = id;
        this.type = type;
        this.entity = Mount::defaultEntity;
    }

    private static Entity defaultEntity() {
        return new Entity(EntityType.ARMOR_STAND);
    }

    public String getId() {
        return id;
    }

    public MountType getType() {
        return type;
    }

    public void setEntity(Supplier<Entity> entity) {
        this.entity = entity;
    }

    Entity createEntity() {
        return entity.get();
    }
}
