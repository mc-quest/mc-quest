package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.loot.LootChest;
import net.minestom.server.event.Event;

public class LootChestCloseEvent implements Event {
    private final PlayerCharacter pc;
    private final LootChest lootChest;

    public LootChestCloseEvent(PlayerCharacter pc, LootChest lootChest) {
        this.pc = pc;
        this.lootChest = lootChest;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    public LootChest getLootChest() {
        return lootChest;
    }
}
