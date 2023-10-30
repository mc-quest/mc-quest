package net.mcquest.server.constants;

import net.mcquest.core.feature.Feature;
import net.mcquest.server.features.*;

public class Features {
    public static final Feature FIGHTER_PLAYER_CLASS = new FighterPlayerClass();
    public static final Feature MAGE_PLAYER_CLASS = new MagePlayerClass();
    public static final Feature ROGUE_PLAYER_CLASS = new RoguePlayerClass();
    public static final Feature SWORDS = new Swords();
    public static final Feature WANDS = new Wands();
    public static final Feature TUTORIAL_QUEST = new TutorialQuest();
    public static final Feature BROODMOTHER_LAIR = new BroodmotherLair();

    public static Feature[] all() {
        return new Feature[]{
                FIGHTER_PLAYER_CLASS,
                MAGE_PLAYER_CLASS,
                ROGUE_PLAYER_CLASS,
                SWORDS,
                WANDS,
                TUTORIAL_QUEST,
                BROODMOTHER_LAIR
        };
    }
}
