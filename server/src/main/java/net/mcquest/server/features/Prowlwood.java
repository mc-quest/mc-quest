package net.mcquest.server.features;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.feature.Feature;
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
                new Pos(1990, 76, 2943),
                new Pos(1990, 73, 2968),
                new Pos(1964, 67, 2988),
                new Pos(1980, 70, 3020),
                new Pos(2001, 72, 3029),
                new Pos(2022, 74, 3031),
                new Pos(2056, 78, 3033),
                new Pos(2059, 71, 3061),
                new Pos(1992, 61, 3074),
                new Pos(1971, 80, 2898),
                new Pos(1937, 70, 2824),
                new Pos(1972, 71, 2799),
                new Pos(2013, 81, 2778),
                new Pos(2020, 73, 2744),
                new Pos(2030, 71, 2720),
                new Pos(2052, 71, 2687),
                new Pos(2124, 76, 2709),
                new Pos(2161, 71, 2728),
                new Pos(2190, 71, 2724),
                new Pos(2204, 82, 2767),
                new Pos(2190, 94, 2805),
                new Pos(2195, 98, 2829),
                new Pos(2190, 104, 2863),
                new Pos(2115, 104, 2857),
                new Pos(2046, 89, 2811)
        }) {
            objectManager.add(ObjectSpawner.of(Instances.ELADRADOR, position, Deer::new));
        }
    }
}
