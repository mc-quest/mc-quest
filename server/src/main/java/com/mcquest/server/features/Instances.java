package com.mcquest.server.features;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.instance.InstanceManager;
import net.minestom.server.instance.Instance;

public class Instances implements Feature {
    private Instance eladrador;

    @Override
    public void hook(Mmorpg mmorpg) {
        InstanceManager instanceManager = mmorpg.getInstanceManager();
        eladrador = instanceManager.getInstance("Eladrador");
    }

    public Instance eladrador() {
        return eladrador;
    }
}
