package com.mcquest.server.features;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.feature.Feature;
import com.mcquest.core.object.ObjectManager;
import com.mcquest.server.constants.Instances;
import com.mcquest.server.npc.Deer;
import net.minestom.server.coordinate.Pos;

public class TestFeature implements Feature {
    @Override
    public void hook(Mmorpg mmorpg) {
        ObjectManager objectManager = mmorpg.getObjectManager();

        Deer deer =  new Deer(mmorpg, Instances.ELADRADOR, new Pos(0, 70, 0));
        objectManager.add(deer);
    }
}
