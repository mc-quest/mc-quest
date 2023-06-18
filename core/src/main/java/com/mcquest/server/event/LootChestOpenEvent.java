package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.loot.LootChest;
import com.mcquest.server.loot.Loot;
import net.minestom.server.event.Event;

import java.util.Collection;
import java.util.Collections;

public class LootChestOpenEvent implements Event {
    private final PlayerCharacter pc;
    private final LootChest lootChest;
    private final Collection<Loot> loot;

    public LootChestOpenEvent(PlayerCharacter pc, LootChest lootChest,
                              Collection<Loot> loot) {
        this.pc = pc;
        this.lootChest = lootChest;
        this.loot = loot;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    public Collection<Loot> getLoot() {
        return Collections.unmodifiableCollection(loot);
    }
}
