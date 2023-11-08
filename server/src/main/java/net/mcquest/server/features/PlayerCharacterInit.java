package net.mcquest.server.features;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.event.PlayerCharacterCreateEvent;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.item.Weapon;
import net.mcquest.server.constants.*;
import net.minestom.server.coordinate.Pos;

public class PlayerCharacterInit implements Feature {
    public void hook(Mmorpg mmorpg) {
        mmorpg.getGlobalEventHandler().addListener(
                PlayerCharacterCreateEvent.class,
                this::handleCreatePlayerCharacter
        );
    }

    private void handleCreatePlayerCharacter(PlayerCharacterCreateEvent event) {
        Weapon weapon = event.getPlayerClass() == PlayerClasses.MAGE ? Items.ADVENTURERS_WAND : Items.ADVENTURERS_SWORD;
        event.setResult(new PlayerCharacterCreateEvent.Result(
                Instances.ELADRADOR,
                new Pos(3200, 117, 3635),
                Instances.ELADRADOR,
                new Pos(3200, 117, 3635),
                Zones.PROWLWOOD_OUTPOST,
                weapon,
                Maps.ELADRADOR,
                Music.VILLAGE,
                20.0,
                20.0,
                2.0,
                2.0
        ));
    }
}
