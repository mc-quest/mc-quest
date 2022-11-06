package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.item.Weapon;

public class PlayerCharacterEquipWeaponEvent {
    private final PlayerCharacter pc;
    private final Weapon weapon;

    public PlayerCharacterEquipWeaponEvent(PlayerCharacter pc, Weapon weapon) {
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
