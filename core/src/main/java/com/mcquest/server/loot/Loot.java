package com.mcquest.server.loot;

import com.mcquest.server.character.PlayerCharacter;
import net.minestom.server.item.ItemStack;

public abstract class Loot {
    private final PoolEntry entry;

    Loot(PoolEntry entry) {
        this.entry = entry;
    }

    public PoolEntry getEntry() {
        return entry;
    }

    abstract ItemStack getItemStack();

    abstract ItemStack loot(PlayerCharacter pc);
}
