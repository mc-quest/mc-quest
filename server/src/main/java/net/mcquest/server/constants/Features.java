package net.mcquest.server.constants;

import net.mcquest.core.feature.Feature;
import net.mcquest.server.features.*;

public class Features {
    public static final Feature PLAYER_CHARACTER_INIT = new PlayerCharacterInit();
    public static final Feature FIGHTER_PLAYER_CLASS = new FighterPlayerClass();
    public static final Feature MAGE_PLAYER_CLASS = new MagePlayerClass();
    public static final Feature ROGUE_PLAYER_CLASS = new RoguePlayerClass();
    public static final Feature MELEE_WEAPONS = new MeleeWeapons();
    public static final Feature WANDS = new Wands();
    public static final Feature CONSUMABLES = new Consumables();
    public static final Feature PROWLWOOD_OUTPOST = new ProwlwoodOutpost();
    public static final Feature PROWLWOOD = new Prowlwood();
    public static final Feature TUTORIAL_QUEST = new TutorialQuest();
    public static final Feature CANINE_CARNAGE = new CanineCarnage();
    public static final Feature WOLF_BITE_DELIGHT = new WolfBiteDelight();
    public static final Feature BROODMOTHER_LAIR = new BroodmotherLair();
    public static final Feature KINGS_DEATH_ROW = new KingsDeathRow();

    public static Feature[] all() {
        return new Feature[]{
                PLAYER_CHARACTER_INIT,
                FIGHTER_PLAYER_CLASS,
                MAGE_PLAYER_CLASS,
                ROGUE_PLAYER_CLASS,
                MELEE_WEAPONS,
                WANDS,
                CONSUMABLES,
                PROWLWOOD_OUTPOST,
                PROWLWOOD,
                TUTORIAL_QUEST,
                CANINE_CARNAGE,
                WOLF_BITE_DELIGHT,
                BROODMOTHER_LAIR,
                KINGS_DEATH_ROW,
                new BetaDemo()
        };
    }
}