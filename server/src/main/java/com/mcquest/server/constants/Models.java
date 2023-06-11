package com.mcquest.server.constants;

import com.mcquest.server.Assets;
import com.mcquest.server.asset.Asset;
import team.unnamed.hephaestus.Model;
import team.unnamed.hephaestus.reader.ModelReader;
import team.unnamed.hephaestus.reader.blockbench.BBModelReader;

public class Models {
    private static final ModelReader reader = BBModelReader.blockbench();

    public static final Model DEER = loadModel("deer_antler");
    public static final Model CROW = loadModel("crow");
    public static final Model WOLF_SPIDER = loadModel("wolf_spider");
    public static final Model REDSTONE_MONSTROSITY = loadModel("redstone_monstrosity");
    public static final Model UNDEAD_KNIGHT = loadModel("undead_knight");

    public static Model[] all() {
        return new Model[]{
                DEER,
                CROW,
                WOLF_SPIDER,
                REDSTONE_MONSTROSITY,
                UNDEAD_KNIGHT
        };
    }

    private static Model loadModel(String name) {
        String path = "models/" + name + ".bbmodel";
        Asset asset = Assets.asset(path);
        return asset.readModel(reader);
    }
}
