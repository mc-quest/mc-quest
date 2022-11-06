package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.item.Weapon;
import net.minestom.server.event.Event;

public class PlayerCharacterUnequipWeaponEvent implements Event {
    private final PlayerCharacter pc;
    private final Weapon weapon;

    public PlayerCharacterUnequipWeaponEvent(PlayerCharacter pc, Weapon weapon) {
        this.pc = pc;
        this.weapon = weapon;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    public Weapon getWeapon() {
        return weapon;
    }
}
