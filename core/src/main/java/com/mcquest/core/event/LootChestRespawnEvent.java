package com.mcquest.core.event;

import com.mcquest.core.loot.LootChest;
import net.minestom.server.event.Event;

public class LootChestRespawnEvent implements Event {
    private final LootChest newLootChest;

    public LootChestRespawnEvent(LootChest newLootChest) {
        this.newLootChest = newLootChest;
    }

    public LootChest getNewLootChest() {
        return newLootChest;
    }
}
