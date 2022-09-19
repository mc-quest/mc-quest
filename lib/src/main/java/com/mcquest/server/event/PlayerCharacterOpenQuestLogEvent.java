package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import net.minestom.server.event.Event;

public class PlayerCharacterOpenQuestLogEvent implements Event {
    private final PlayerCharacter pc;

    public PlayerCharacterOpenQuestLogEvent(PlayerCharacter pc) {
        this.pc = pc;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }
}
