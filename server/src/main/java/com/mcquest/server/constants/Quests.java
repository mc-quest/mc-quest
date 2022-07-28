package com.mcquest.server.constants;

import com.mcquest.server.quest.Quest;
import com.mcquest.server.quest.QuestManager;

/**
 * Provides references to Quests loaded by the QuestLoader.
 */
public class Quests {
    public static Quest TUTORIAL = QuestManager.getQuest("Tutorial");
}
