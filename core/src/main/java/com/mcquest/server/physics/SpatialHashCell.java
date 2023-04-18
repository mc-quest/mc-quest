package com.mcquest.server.physics;

import com.mcquest.server.instance.Instance;
import net.minestom.server.coordinate.Pos;

import java.util.Objects;

public class SpatialHashCell {
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