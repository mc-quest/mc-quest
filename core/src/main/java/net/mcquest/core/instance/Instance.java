package net.mcquest.core.instance;

import net.minestom.server.instance.IChunkLoader;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.world.DimensionType;

import java.util.UUID;

public class Instance extends InstanceContainer {
    private final String id;

    private Instance(Builder builder) {
        super(UUID.randomUUID(), builder.dimensionType);
        this.id = builder.id;
        setChunkLoader(builder.loader);
    }

    public String getId() {
        return id;
    }

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public static class Builder {
        private final String id;
        private DimensionType dimensionType;
        private IChunkLoader loader;

        private Builder(String id) {
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
