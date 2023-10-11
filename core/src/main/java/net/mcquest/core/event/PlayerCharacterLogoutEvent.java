package net.mcquest.core.event;

import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.ui.PlayerCharacterLogoutType;
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
