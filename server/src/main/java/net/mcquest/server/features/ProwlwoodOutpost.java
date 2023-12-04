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
                new Pos(2004, 73, 2884),
                new Pos(2186, 131, 3018)
        );
        bounds.onCollisionEnter(Triggers.playerCharacter(this::handleEnter));
        bounds.onCollisionExit(Triggers.playerCharacter(this::handleExit));
        mmorpg.getPhysicsManager().addCollider(bounds);
    }

    private void adventurers() {
        Pos[] positions = {
                new Pos(2020, 79, 2942),
                new Pos(2007, 83, 2895),
                new Pos(2036, 87, 2886),
                new Pos(2066, 89, 2888),
                new Pos(2077, 88, 2928),
                new Pos(2099, 89, 2926),
                new Pos(2113, 88, 2924),
                new Pos(2129, 88, 2929),
                new Pos(2140, 88, 2914),
                new Pos(2096, 89, 2953),
                new Pos(2100, 88, 2970),
                new Pos(2114, 89, 2967),
                new Pos(2131, 89, 2957),
                new Pos(2140, 88, 2973),
                new Pos(2160, 89, 2972),
                new Pos(2171, 89, 2962),
                new Pos(2165, 88, 2943),
                new Pos(2098, 89, 2981),
                new Pos(2116, 87, 3004),
                new Pos(2110, 87, 3010),
                new Pos(2085, 83, 3004),
                new Pos(2056, 84, 2974),
                new Pos(2032, 84, 2964),
                new Pos(2014, 78, 2947),
                new Pos(2090, 88, 2933),
                new Pos(2102, 89, 2926)
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
