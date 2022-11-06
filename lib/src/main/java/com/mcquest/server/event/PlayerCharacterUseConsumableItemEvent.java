package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.item.ConsumableItem;

public class PlayerCharacterUseConsumableItemEvent {
    private final PlayerCharacter pc;
    private final ConsumableItem item;

    public PlayerCharacterUseConsumableItemEvent(PlayerCharacter pc, ConsumableItem item) {
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
