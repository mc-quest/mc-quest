package com.mcquest.server.features;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.feature.Feature;
import com.mcquest.core.object.ObjectManager;
import com.mcquest.core.object.ObjectSpawner;
import com.mcquest.server.constants.Instances;
import com.mcquest.server.npc.TrainingDummy;
import net.minestom.server.coordinate.Pos;

public class TestFeature implements Feature {
    @Override
    public void hook(Mmorpg mmorpg) {
        ObjectManager objectManager = mmorpg.getObjectManager();
        objectManager.add(new ObjectSpawner(
                Instances.ELADRADOR,
                new Pos(0, 69, 0),
                TrainingDummy::new
        ));
    }
}
