package net.mcquest.server.constants;

import net.mcquest.core.asset.Asset;
import net.mcquest.server.Assets;
import team.unnamed.hephaestus.Model;

public class Models {
    public static final Model TRAINING_DUMMY = loadModel("training_dummy");
    public static final Model DEER = loadModel("deer_antler");
    public static final Model CROW = loadModel("crow");
    public static final Model WOLF_SPIDER = loadModel("wolf_spider");
    public static final Model BROODMOTHER = loadModel("broodmother");
    public static final Model REDSTONE_MONSTROSITY = loadModel("redstone_monstrosity");
    public static final Model UNDEAD_KNIGHT = loadModel("undead_knight");

    public static Model[] all() {
        return new Model[]{
                TRAINING_DUMMY,
                DEER,
                CROW,
                WOLF_SPIDER,
                BROODMOTHER,
                REDSTONE_MONSTROSITY,
                UNDEAD_KNIGHT
        };
    }

    private static Model loadModel(String name) {
        String path = "models/" + name + ".bbmodel";
        Asset asset = Assets.asset(path);
        return asset.readModel();
    }
}
