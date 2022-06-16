package com.mcquest.server.main.load;

import com.mcquest.server.api.quest.Quest;
import com.mcquest.server.api.quest.QuestManager;

public class QuestLoader {
    /**
     * Loads all Quests and registers them with the QuestManager.
     */
    public static void loadQuests() {
        // TODO
        Quest[] quests = null;
        for (Quest quest : quests) {
            QuestManager.registerQuest(quest);
        }
    }
}
