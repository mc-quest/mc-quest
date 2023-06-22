package com.mcquest.core.instance;

import java.util.Objects;

public class ChunkAddress {
    private final Instance instance;
    private final int x;
    private final int z;

    public ChunkAddress(Instance instance, int x, int z) {
        this.instance = instance;
        this.x = x;
        this.z = z;
    }

    public Instance getInstance() {
        return instance;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ChunkAddress address)) {
            return false;
        }

        return this.instance == address.instance
                && this.x == address.x
                && this.z == address.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instance, x, z);
    }
}
