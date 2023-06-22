package com.mcquest.core.event;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.item.Weapon;
import net.minestom.server.event.Event;

public class WeaponUnequipEvent implements Event {
    private final PlayerCharacter pc;
    private final Weapon weapon;

    public WeaponUnequipEvent(PlayerCharacter pc, Weapon weapon) {
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
