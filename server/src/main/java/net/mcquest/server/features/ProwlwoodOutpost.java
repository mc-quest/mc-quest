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
                new Pos(3269, 121, 3605),
                new Pos(3221, 116, 3642),
                new Pos(3209, 117, 3650),
                new Pos(3226, 117, 3654),
                new Pos(3238, 118, 3643),
                new Pos(3244, 122, 3625),
                new Pos(3234, 120, 3602),
                new Pos(3216, 118, 3597),
                new Pos(3200, 114, 3607),
                new Pos(3182, 111, 3618),
                new Pos(3174, 114, 3583),
                new Pos(3193, 119, 3571),
                new Pos(3218, 120, 3566),
                new Pos(3245, 122, 3569),
                new Pos(3256, 122, 3602),
                new Pos(3226, 117, 3642),
                new Pos(3237, 118, 3640),
                new Pos(3244, 119, 3633),
                new Pos(3252, 122, 3620),
                new Pos(3263, 121, 3622),
                new Pos(3265, 121, 3639),
                new Pos(3283, 121, 3641)
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