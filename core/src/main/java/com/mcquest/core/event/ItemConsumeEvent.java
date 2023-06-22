package com.mcquest.core.event;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.item.ConsumableItem;
import net.minestom.server.event.Event;

public class ItemConsumeEvent implements Event {
    private final PlayerCharacter pc;
    private final ConsumableItem item;

    public ItemConsumeEvent(PlayerCharacter pc, ConsumableItem item) {
        this.pc = pc;
        this.item = item;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    public ConsumableItem getItem() {
        return item;
    }
}
