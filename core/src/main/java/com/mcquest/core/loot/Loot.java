package com.mcquest.core.loot;

import com.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Pos;

public abstract class Loot {
    private final PoolEntry entry;

    Loot(PoolEntry entry) {
        this.entry = entry;
    }

    public PoolEntry getEntry() {
        return entry;
    }

    abstract void drop(Instance instance, Pos position);
}
