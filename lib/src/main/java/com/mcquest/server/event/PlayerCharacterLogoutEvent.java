package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.ui.PlayerCharacterLogoutType;
import net.minestom.server.event.Event;

public class PlayerCharacterLogoutEvent implements Event {
    private final PlayerCharacter pc;
    private final PlayerCharacterLogoutType logoutType;

    public PlayerCharacterLogoutEvent(PlayerCharacter pc, PlayerCharacterLogoutType logoutType) {
        this.pc = pc;
        this.logoutType = logoutType;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    public PlayerCharacterLogoutType getLogoutType() {
        return logoutType;
    }
}
