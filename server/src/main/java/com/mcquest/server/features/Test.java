package com.mcquest.server.features;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.npc.Wolf;
import com.mcquest.server.physics.PhysicsManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

public class Test implements Feature {
    @Override
    public void hook(Mmorpg mmorpg) {
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();
        Instance instance = null; // mmorpg.getInstanceManager().getInstance("eladrador");
        Pos spawnPosition = new Pos(0, 70, 0);
        Wolf wolf = new Wolf(physicsManager, instance, spawnPosition);
    }
}
