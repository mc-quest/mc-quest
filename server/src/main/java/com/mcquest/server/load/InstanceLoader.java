package com.mcquest.server.load;

import com.mcquest.server.instance.InstanceManager;
import com.mcquest.server.util.ResourceLoader;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;

public class InstanceLoader {
    public void loadInstances(InstanceManager instanceManager) {
        ResourceLoader.extractResources("worlds", "world");
        loadInstance("Eladrador", "world/eladrador", instanceManager);
    }

    private static void loadInstance(String name, String path, InstanceManager instanceManager) {
        InstanceContainer instance = instanceManager.createInstanceContainer(name);
        instance.setChunkLoader(new AnvilLoader(path));
    }
}
