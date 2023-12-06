package net.mcquest.server.features;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.event.PlayerCharacterCreateEvent;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.item.Weapon;
import net.mcquest.core.playerclass.PlayerClass;
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
        Pos spawnPosition = new Pos(2095, 89, 2943, 90, 0);
        PlayerClass playerClass = event.getPlayerClass();

        Weapon weapon;
        if (playerClass == PlayerClasses.MAGE) {
            weapon = Items.ADVENTURERS_WAND;
        } else if (playerClass == PlayerClasses.FIGHTER) {
            weapon = Items.ADVENTURERS_SWORD;
        } else if (playerClass == PlayerClasses.ROGUE) {
            weapon = Items.IRON_DAGGER;
        } else {
            throw new RuntimeException("Unknown player class: " + playerClass.getName());
        }

        event.setResult(new PlayerCharacterCreateEvent.Result(
                Instances.ELADRADOR,
                spawnPosition,
                Instances.ELADRADOR,
                spawnPosition,
                Zones.PROWLWOOD_OUTPOST,
                weapon,
                Maps.ELADRADOR,
                Music.VILLAGE,
                20.0,
                20.0,
                1.0,
                2.0
        ));
    }
}
