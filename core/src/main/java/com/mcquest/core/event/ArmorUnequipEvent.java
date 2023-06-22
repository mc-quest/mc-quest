package com.mcquest.core.event;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.item.ArmorItem;
import net.minestom.server.event.Event;

public class ArmorUnequipEvent implements Event {
    private final PlayerCharacter pc;
    private final ArmorItem item;

    public ArmorUnequipEvent(PlayerCharacter pc, ArmorItem item) {
        this.pc = pc;
        this.item = item;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    public ArmorItem getItem() {
        return item;
    }
}
