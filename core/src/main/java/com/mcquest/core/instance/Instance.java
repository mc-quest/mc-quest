package com.mcquest.core.instance;

import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.world.DimensionType;

import java.util.UUID;

public class Instance extends InstanceContainer {
    private final int id;

    private Instance(Builder builder) {
        super(UUID.randomUUID(), builder.dimensionType);
        this.id = builder.id;
        setChunkLoader(builder.loader);
    }

    public int getId() {
        return id;
    }

    public static Builder builder(int id) {
        return new Builder(id);
    }

    public static class Builder {
        private final int id;
        private DimensionType dimensionType;
        private IChunkLoader loader;

        private Builder(int id) {
            this.id = id;
            dimensionType = DimensionType.OVERWORLD;
            loader = null;
        }

        public Builder dimensionType(DimensionType dimensionType) {
            this.dimensionType = dimensionType;
            return this;
        }

        public Builder chunkLoader(IChunkLoader loader) {
            this.loader = loader;
            return this;
        }

        public Instance build() {
            return new Instance(this);
        }
    }
}
