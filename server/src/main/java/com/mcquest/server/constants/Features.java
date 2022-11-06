package com.mcquest.server.constants;

import com.mcquest.server.feature.Feature;
import com.mcquest.server.features.FighterPlayerClass;
import com.mcquest.server.features.MagePlayerClass;
import com.mcquest.server.features.TutorialQuest;

public class Features {
    public static final Feature FIGHTER_PLAYER_CLASS = new FighterPlayerClass();
    public static final Feature MAGE_PLAYER_CLASS = new MagePlayerClass();
    public static final Feature TUTORIAL_QUEST = new TutorialQuest();
}
