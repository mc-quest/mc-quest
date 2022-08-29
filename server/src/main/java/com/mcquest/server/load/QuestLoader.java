package com.mcquest.server.load;

import com.mcquest.server.quest.Quest;
import com.mcquest.server.quest.QuestManager;
import com.mcquest.server.util.ResourceLoader;

import java.util.List;

public class QuestLoader {
    /**
     * Loads all Quests and registers them with the QuestManager.
     */
    public static void loadQuests() {
        List<String> questPaths = ResourceLoader.getResources("quests");
        for (String questPath : questPaths) {
            Quest quest = ResourceLoader.deserializeJsonResource(questPath, Quest.class);
            QuestManager.registerQuest(quest);
        }
    }
}
