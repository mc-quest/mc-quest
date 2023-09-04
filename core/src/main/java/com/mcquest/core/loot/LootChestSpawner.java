package com.mcquest.core.loot;

import com.mcquest.core.instance.Instance;
import com.mcquest.core.object.ObjectProvider;
import com.mcquest.core.object.ObjectSpawner;
import net.minestom.server.coordinate.Pos;

public class LootChestSpawner {
    public static ObjectSpawner spawner(Instance instance, Pos position, LootTable lootTable) {
        return new ObjectSpawner(instance, position, provider(lootTable));
    }

    private static ObjectProvider provider(LootTable lootTable) {
        return (mmorpg, spawner) ->
                new LootChest(mmorpg, spawner, lootTable);
    }
}
