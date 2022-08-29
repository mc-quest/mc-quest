package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.item.Weapon;
import net.minestom.server.event.Event;

public class PlayerCharacterBasicAttackEvent implements Event {
    private final PlayerCharacter pc;

    public PlayerCharacterBasicAttackEvent(PlayerCharacter pc) {
        this.pc = pc;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }
}
