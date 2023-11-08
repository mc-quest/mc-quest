package net.mcquest.server.features;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.object.ObjectManager;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.core.physics.Collider;
import net.mcquest.core.physics.Triggers;
import net.mcquest.server.constants.Instances;
import net.mcquest.server.constants.Music;
import net.mcquest.server.constants.Zones;
import net.mcquest.server.npc.Adventurer;
import net.minestom.server.coordinate.Pos;

public class ProwlwoodOutpost implements Feature {
    private Mmorpg mmorpg;

    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        bounds();
        adventurers();
    }

    private void bounds() {
        Collider bounds = new Collider(
                Instances.ELADRADOR,
                new Pos(3169, 80, 3547),
                new Pos(3361, 142, 3682)
        );
        bounds.onCollisionEnter(Triggers.playerCharacter(this::handleEnter));
        bounds.onCollisionExit(Triggers.playerCharacter(this::handleExit));
        mmorpg.getPhysicsManager().addCollider(bounds);
    }

    private void adventurers() {
        Pos[] positions = {
                new Pos(3269, 121.0, 3605, 175.2f, 17.5f)
        };

        ObjectManager objectManager = mmorpg.getObjectManager();
        for (Pos position : positions) {
            objectManager.add(ObjectSpawner.of(Instances.ELADRADOR, position, Adventurer::new));
        }
    }

    private void handleEnter(PlayerCharacter pc) {
        pc.setZone(Zones.PROWLWOOD_OUTPOST);
        pc.getMusicPlayer().setSong(Music.VILLAGE);
    }

    private void handleExit(PlayerCharacter pc) {
        pc.setZone(Zones.PROWLWOOD);
        pc.getMusicPlayer().setSong(Music.WILDERNESS);
    }
}