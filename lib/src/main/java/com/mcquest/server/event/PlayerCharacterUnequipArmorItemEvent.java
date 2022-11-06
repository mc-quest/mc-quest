package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.item.ArmorItem;
import net.minestom.server.event.Event;

public class PlayerCharacterUnequipArmorItemEvent implements Event {
    private final PlayerCharacter pc;
    private final ArmorItem item;

    public PlayerCharacterUnequipArmorItemEvent(PlayerCharacter pc, ArmorItem item) {
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
