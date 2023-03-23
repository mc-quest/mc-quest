package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import net.minestom.server.event.Event;

public class MapCloseEvent implements Event {
    private final PlayerCharacter pc;

    public MapCloseEvent(PlayerCharacter pc) {
        this.pc = pc;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }
}
