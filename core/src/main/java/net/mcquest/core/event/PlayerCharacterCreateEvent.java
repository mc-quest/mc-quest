package net.mcquest.core.event;

import net.mcquest.core.cartography.Map;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.item.Weapon;
import net.mcquest.core.music.Song;
import net.mcquest.core.playerclass.PlayerClass;
import net.mcquest.core.zone.Zone;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.Event;

public class PlayerCharacterCreateEvent implements Event {
    private final PlayerClass playerClass;
    private Result result;

    public PlayerCharacterCreateEvent(PlayerClass playerClass) {
        this.playerClass = playerClass;
        result = null;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public record Result(
            Instance instance,
            Pos position,
            Instance respawnInstance,
            Pos respawnPosition,
            Zone zone,
            Weapon weapon,
            Map map,
            Song song,
            double maxHealth,
            double maxMana,
            double healthRegenRate,
            double manaRegenRate
    ) {
    }
}
