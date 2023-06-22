package com.mcquest.core.event;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.ui.PlayerCharacterLogoutType;
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
