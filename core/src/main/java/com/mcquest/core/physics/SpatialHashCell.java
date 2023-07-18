package com.mcquest.core.physics;

import com.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Pos;

import java.util.Objects;
import java.util.function.Consumer;

public final class SpatialHashCell {
    private final Instance instance;
    private final int x;
    private final int y;
    private final int z;

    public SpatialHashCell(Instance instance, int x, int y, int z) {
        this.instance = instance;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static SpatialHashCell cellAt(Instance instance, Pos position, double cellSize) {
        int x = (int) Math.floor(position.x() / cellSize);
        int y = (int) Math.floor(position.y() / cellSize);
        int z = (int) Math.floor(position.z() / cellSize);
        return new SpatialHashCell(instance, x, y, z);
    }

    public Instance getInstance() {
        return instance;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public static void forAllInRange(SpatialHashCell min, SpatialHashCell max, Consumer<SpatialHashCell> consumer) {
        if (min.instance != max.instance) {
            throw new IllegalArgumentException();
        }

        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    consumer.accept(new SpatialHashCell(min.instance, x, y, z));
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof SpatialHashCell cell)) {
            return false;
        }

        return this.instance == cell.instance && this.x == cell.x
                && this.y == cell.y && this.z == cell.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instance, x, y, z);
    }
}