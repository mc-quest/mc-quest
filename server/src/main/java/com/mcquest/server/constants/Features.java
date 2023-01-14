package com.mcquest.server.constants;

import com.mcquest.server.feature.Feature;
import com.mcquest.server.features.FighterPlayerClass;
import com.mcquest.server.features.MagePlayerClass;
import com.mcquest.server.features.Swords;
import com.mcquest.server.features.TutorialQuest;

public class Features {
    public static final Feature FIGHTER_PLAYER_CLASS = new FighterPlayerClass();
    public static final Feature MAGE_PLAYER_CLASS = new MagePlayerClass();
    public static final Feature SWORDS = new Swords();
    public static final Feature TUTORIAL_QUEST = new TutorialQuest();

    public static Feature[] all() {
        return new Feature[]{
                FIGHTER_PLAYER_CLASS,
                MAGE_PLAYER_CLASS,
                SWORDS,
                TUTORIAL_QUEST
        };
    }
}
