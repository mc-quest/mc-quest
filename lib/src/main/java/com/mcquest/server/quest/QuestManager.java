package com.mcquest.server.quest;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The QuestManager is used to register and retrieve Quests.
 */
public class QuestManager {
    private final Map<Integer, Quest> questsById;

    public QuestManager(Quest[] quests) {
        questsById = new HashMap<>();
        for (Quest quest : quests) {
            registerQuest(quest);
        }
    }

    private void registerQuest(Quest quest) {
        int id = quest.getId();
        if (questsById.containsKey(id)) {
            throw new IllegalArgumentException("ID already in use: " + id);
        }
        questsById.put(id, quest);
    }

    /**
     * Returns the registered Quest with the given ID, or null if none
     * exists.
     */
    public Quest getQuest(int id) {
        return questsById.get(id);
    }

    public Collection<Quest> getQuests() {
        return Collections.unmodifiableCollection(questsById.values());
    }
}
