package net.mcquest.core.event;

import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.loot.Loot;
import net.mcquest.core.loot.LootChest;
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
