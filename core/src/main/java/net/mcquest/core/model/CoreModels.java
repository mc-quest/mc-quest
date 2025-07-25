package net.mcquest.core.model;

import net.mcquest.core.asset.Asset;
import team.unnamed.hephaestus.Model;

public class CoreModels {
    public static final Model LOOT_CHEST = loadModel("models/loot_chest.bbmodel");

    static final Model[] all() {
        return new Model[]{
                LOOT_CHEST
        };
    }

    private static Model loadModel(String path) {
        Asset asset = Asset.of(CoreModels.class.getClassLoader(), path);
        return asset.readModel();
    }
}
