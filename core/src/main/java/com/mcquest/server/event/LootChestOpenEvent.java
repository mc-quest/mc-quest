package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.loot.LootChest;
import com.mcquest.server.loot.Loot;
import net.minestom.server.event.Event;

public class LootChestOpenEvent implements Event {
    private final PlayerCharacter pc;
    private final LootChest lootChest;
    private final Loot[] loot;

    public LootChestOpenEvent(PlayerCharacter pc, LootChest lootChest,
                              Loot[] loot) {
        this.pc = pc;
        this.lootChest = lootChest;
        this.loot = loot;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    public Loot[] getLoot() {
        return loot.clone();
    }
}
