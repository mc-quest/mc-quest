package com.mcquest.server.loot;

import com.mcquest.server.character.PlayerCharacter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class LootTable {
    private final Collection<Pool> pools;

    private LootTable(Builder builder) {
        pools = builder.pools;
    }

    public Collection<Pool> getPools() {
        return Collections.unmodifiableCollection(pools);
    }

    public Collection<Loot> generate(PlayerCharacter pc) {
        Collection<Loot> loot = new ArrayList<>();
        for (Pool pool : pools) {
            loot.addAll(pool.generate(pc));
        }
        return loot;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Collection<Pool> pools;

        private Builder() {
            this.pools = new ArrayList<>();
        }

        public Builder pool(Pool pool) {
            pools.add(pool);
            return this;
        }

        public LootTable build() {
            return new LootTable(this);
        }
    }
}
