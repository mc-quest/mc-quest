package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import net.minestom.server.event.Event;

public class QuestLogOpenEvent implements Event {
    private final PlayerCharacter pc;

    public QuestLogOpenEvent(PlayerCharacter pc) {
        this.pc = pc;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }
}
