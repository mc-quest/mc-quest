package com.mcquest.server.api.event;

import com.mcquest.server.api.character.PlayerCharacter;
import com.mcquest.server.api.item.Item;
import net.minestom.server.event.Event;

public class PlayerCharacterReceiveItemEvent implements Event {
    private final PlayerCharacter pc;
    private final Item item;
    private final int amountReceived;

    public PlayerCharacterReceiveItemEvent(PlayerCharacter pc, Item item, int amountReceived) {
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
