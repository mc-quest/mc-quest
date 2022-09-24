package com.mcquest.server.features;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.constants.Instances;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.instance.InstanceManager;
import com.mcquest.server.util.ResourceUtility;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;

public class InstanceChunkLoaders implements Feature {
    @Override
    public void hook(Mmorpg mmorpg) {
        ResourceUtility.extractResources("worlds", "world");
        InstanceManager instanceManager = mmorpg.getInstanceManager();
        InstanceContainer eladrador = instanceManager.getInstance(Instances.ELADRADOR);
        eladrador.setChunkLoader(new AnvilLoader("world/eladrador"));
    }
}
