package com.mcquest.server.load;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.constants.Instances;
import com.mcquest.server.instance.InstanceManager;

public class InstanceLoader {
    public static void loadInstances(Mmorpg mmorpg) {
        InstanceManager instanceManager = mmorpg.getInstanceManager();
        instanceManager.createInstance(Instances.ELADRADOR);
    }
}
