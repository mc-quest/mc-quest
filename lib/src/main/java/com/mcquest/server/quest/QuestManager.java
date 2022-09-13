package com.mcquest.server.quest;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The QuestManager is used to register and retrieve Quests.
 */
public class QuestManager {
    private final Map<Integer, Quest> questsById;

    public QuestManager() {
        questsById = new HashMap<>();
    }

    void registerQuest(@NotNull Quest quest) {
        int id = quest.getId();
        if (questsById.containsKey(id)) {
            throw new IllegalArgumentException("ID of " + quest.getName() + " is already in use");
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
        return questsById.values();
    }

    public QuestBuilder questBuilder(int id, String name, int level) {
        return new QuestBuilder(this, id, name, level);
    }
}
