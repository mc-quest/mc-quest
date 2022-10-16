package com.mcquest.server.load;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.constants.Instances;
import com.mcquest.server.instance.InstanceManager;
import com.mcquest.server.util.ResourceUtility;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;

public class InstanceLoader {
    public static void loadInstances(Mmorpg mmorpg) {
        InstanceManager instanceManager = mmorpg.getInstanceManager();
        InstanceContainer eladrador = instanceManager.createInstance(Instances.ELADRADOR);
        ResourceUtility.extractResources("worlds", "world");
        eladrador.setChunkLoader(new AnvilLoader("world/eladrador"));
    }
}
