package com.mcquest.core.event;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.item.Item;
import net.minestom.server.event.Event;

public class ItemRemoveEvent implements Event {
    private final PlayerCharacter pc;
    private final Item item;
    private final int amountRemoved;

    public ItemRemoveEvent(PlayerCharacter pc, Item item, int amountRemoved) {
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
