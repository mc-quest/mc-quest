package net.mcquest.core.event;

import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.item.Weapon;
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
