package com.mcquest.server.constants;

import com.mcquest.server.util.ResourceUtility;
import team.unnamed.hephaestus.Model;

public class Models {
    public static final Model DEER = loadModel("deer_antler");

    private static Model loadModel(String fileName) {
        String path = "models/" + fileName + ".bbmodel";
        return ResourceUtility.readModel(path);
    }
}
