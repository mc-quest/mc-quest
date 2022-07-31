package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import net.minestom.server.event.Event;

public class PlayerCharacterRegisterEvent implements Event {
    private final PlayerCharacter pc;

    public PlayerCharacterRegisterEvent(PlayerCharacter pc) {
        this.pc = pc;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }
}
