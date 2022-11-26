package com.mcquest.server.physics;

import com.mcquest.server.instance.Instance;

import java.util.Objects;

class ColliderBucketAddress {
    private final Instance instance;
    private final int x;
    private final int y;
    private final int z;

    ColliderBucketAddress(Instance instance, int x, int y, int z) {
        this.instance = instance;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof ColliderBucketAddress)) {
            return false;
        }

        ColliderBucketAddress address = (ColliderBucketAddress) o;
        return this.instance == address.instance && this.x == address.x
                && this.y == address.y && this.z == address.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instance, x, y, z);
    }
}