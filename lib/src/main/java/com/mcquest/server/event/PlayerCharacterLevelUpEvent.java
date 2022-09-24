package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;

public class PlayerCharacterLevelUpEvent {
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
