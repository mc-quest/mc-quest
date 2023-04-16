package com.mcquest.server.constants;

import com.mcquest.server.Assets;
import com.mcquest.server.asset.Asset;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.reader.ModelReader;
import team.unnamed.hephaestus.reader.blockbench.BBModelReader;

public class Models {
    private static final ModelReader reader = BBModelReader.blockbench();
    public static final Model DEER = loadModel("deer_antler");

    public static Model[] all() {
        return new Model[]{
                DEER
        };
    }

    private static Model loadModel(String name) {
        String path = "models/" + name + ".bbmodel";
        Asset asset = Assets.asset(path);
        return asset.readModel(reader);
    }
}
