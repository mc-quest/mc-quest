package com.mcquest.server.load;

import com.mcquest.server.world.InstanceManager;
import com.mcquest.server.util.ResourceLoader;
import net.minestom.server.MinecraftServer;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.InstanceContainer;

public class InstanceLoader {
    public static void loadInstances() {
        ResourceLoader.extractResources("worlds", "world");
        loadInstance("Lobby", "world/lobby");
        loadInstance("Eladrador", "world/eladrador");
    }

    private static void loadInstance(String name, String path) {
        InstanceContainer instance =
                MinecraftServer.getInstanceManager().createInstanceContainer();
        instance.setChunkLoader(new AnvilLoader(path));
        InstanceManager.register(name, instance);
    }
}
