package com.mcquest.core.event;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.item.Item;
import net.minestom.server.event.Event;

public class ItemReceiveEvent implements Event {
    private final PlayerCharacter pc;
    private final Item item;
    private final int amountReceived;

    public ItemReceiveEvent(PlayerCharacter pc, Item item, int amountReceived) {
        this.pc = pc;
        this.item = item;
        this.amountReceived = amountReceived;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    public Item getItem() {
        return item;
    }

    public int getAmountReceived() {
        return amountReceived;
    }
}
