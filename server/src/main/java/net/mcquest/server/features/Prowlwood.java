package net.mcquest.server.features;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.object.ObjectManager;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.server.constants.Instances;
import net.mcquest.server.npc.Deer;
import net.minestom.server.coordinate.Pos;

public class Prowlwood implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        deer();
    }

    private void deer() {
        ObjectManager objectManager = mmorpg.getObjectManager();

        for (Pos position : new Pos[]{
                new Pos(3144, 104, 3646),
                new Pos(3132, 100, 3683),
                new Pos(3106, 98, 3676),
                new Pos(3083, 96, 3673),
                new Pos(3063, 98, 3663),
                new Pos(3047, 101, 3634),
                new Pos(3019, 102, 3611),
                new Pos(3034, 106, 3585),
                new Pos(3045, 109, 3570),
                new Pos(3006, 109, 3552),
                new Pos(3103, 98, 3674),
                new Pos(3085, 95, 3720),
                new Pos(3045, 93, 3743)
        }) {
            objectManager.add(ObjectSpawner.of(Instances.ELADRADOR, position, Deer::new));
        }
    }
}
