package com.mcquest.server.api.event;

import com.mcquest.server.api.character.PlayerCharacter;
import com.mcquest.server.api.item.Item;
import net.minestom.server.event.Event;

public class PlayerCharacterRemoveItemEvent implements Event {
    private final PlayerCharacter pc;
    private final Item item;
    private final int amountRemoved;

    public PlayerCharacterRemoveItemEvent(PlayerCharacter pc, Item item, int amountRemoved) {
        this.pc = pc;
        this.item = item;
        this.amountRemoved = amountRemoved;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    public Item getItem() {
        return item;
    }

    public int getAmountRemoved() {
        return amountRemoved;
    }
}
