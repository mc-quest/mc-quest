package com.mcquest.server.loot;

import com.mcquest.server.instance.Instance;
import net.minestom.server.coordinate.Pos;

public class ItemLoot extends Loot {
    private final int amount;

    ItemLoot(ItemPoolEntry entry, int amount) {
        super(entry);
        this.amount = amount;
    }

    @Override
    public ItemPoolEntry getEntry() {
        return (ItemPoolEntry) super.getEntry();
    }

    void drop(Instance instance, Pos position) {
        getEntry().getItem().drop(instance, position, amount);
    }
}
