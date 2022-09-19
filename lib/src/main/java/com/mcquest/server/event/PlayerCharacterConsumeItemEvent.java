package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.item.ConsumableItem;
import net.minestom.server.event.Event;

public class PlayerCharacterConsumeItemEvent implements Event {
    private final PlayerCharacter pc;
    private final ConsumableItem item;

    public PlayerCharacterConsumeItemEvent(PlayerCharacter pc, ConsumableItem item) {
        this.pc = pc;
        this.item = item;
    }

    public PlayerCharacter getPlayerCharcter() {
        return pc;
    }

    public ConsumableItem getItem() {
        return item;
    }
}
