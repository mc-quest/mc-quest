package com.mcquest.server.load;

import com.mcquest.server.quest.Quest;
import com.mcquest.server.quest.QuestManager;
import com.mcquest.server.util.ResourceLoader;

import java.util.List;

public class QuestLoader {
    public static void loadQuests(QuestManager questManager) {
        List<String> questPaths = ResourceLoader.getResources("quests");
        for (String questPath : questPaths) {
        }
    }
}
