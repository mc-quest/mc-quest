package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import net.minestom.server.event.Event;

public class PlayerCharacterLevelUpEvent implements Event {
    private final PlayerCharacter pc;
    private final int newLevel;

    public PlayerCharacterLevelUpEvent(PlayerCharacter pc, int newLevel) {
        this.pc = pc;
        this.newLevel = newLevel;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    public int getNewLevel() {
        return newLevel;
    }
}
