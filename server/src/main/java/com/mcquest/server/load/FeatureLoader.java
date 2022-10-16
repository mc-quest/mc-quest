package com.mcquest.server.load;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.feature.FeatureManager;
import com.mcquest.server.features.*;

public class FeatureLoader {
    public static void loadFeatures(Mmorpg mmorpg) {
        FeatureManager featureManager = mmorpg.getFeatureManager();
        featureManager.addFeatures(
                new TestFeature(),
                new FighterPlayerClass(),
                new MagePlayerClass(),
                new Tutorial()
        );
    }
}
