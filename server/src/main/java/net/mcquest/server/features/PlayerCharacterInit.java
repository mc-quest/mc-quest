package net.mcquest.server.features;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.event.PlayerCharacterCreateEvent;
import net.mcquest.core.feature.Feature;
import net.mcquest.server.constants.Instances;
import net.mcquest.server.constants.Items;
import net.mcquest.server.constants.Zones;
import net.minestom.server.coordinate.Pos;

public class PlayerCharacterInit implements Feature {
    public void hook(Mmorpg mmorpg) {
        mmorpg.getGlobalEventHandler().addListener(
                PlayerCharacterCreateEvent.class,
                this::handleCreatePlayerCharacter
        );
    }

    private void handleCreatePlayerCharacter(PlayerCharacterCreateEvent event) {
        event.setResult(new PlayerCharacterCreateEvent.Result(
                Instances.ELADRADOR,
                new Pos(2892, 86, 3225),
                Instances.ELADRADOR,
                new Pos(2892, 86, 3225),
                Zones.OAKSHIRE,
                Items.ADVENTURERS_SWORD
        ));
    }
}
