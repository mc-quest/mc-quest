package com.mcquest.core.event;

import com.mcquest.core.character.PlayerCharacter;
import net.minestom.server.event.Event;

public class MenuOpenEvent implements Event {
    private final PlayerCharacter pc;

    public MenuOpenEvent(PlayerCharacter pc) {
        this.pc = pc;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }
}
