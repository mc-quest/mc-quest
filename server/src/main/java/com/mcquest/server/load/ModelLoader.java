package com.mcquest.server.load;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.resource.ResourceManager;
import com.mcquest.server.util.ResourceUtility;
import team.unnamed.hephaestus.Model;

import java.util.List;

public class ModelLoader {
    public static void loadModels(Mmorpg mmorpg) {
        ResourceManager resourceManager = mmorpg.getResourceManager();
        List<String> paths = ResourceUtility.getResources("models");
        for (String path : paths) {
            Model model = ResourceUtility.readModel(path);
            resourceManager.registerModel(model);
        }
    }
}
