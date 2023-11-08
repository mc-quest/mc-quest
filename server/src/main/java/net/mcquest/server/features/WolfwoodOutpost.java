package net.mcquest.server.features;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.object.ObjectManager;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.server.constants.Instances;
import net.mcquest.server.npc.Adventurer;
import net.minestom.server.coordinate.Pos;

public class WolfwoodOutpost implements Feature {
    private Mmorpg mmorpg;

    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        adventurers();
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
}