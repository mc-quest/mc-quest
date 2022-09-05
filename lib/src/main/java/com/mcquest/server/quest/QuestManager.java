package com.mcquest.server.quest;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The QuestManager is used to register and retrieve Quests.
 */
public class QuestManager {
    private final Map<String, Quest> quests;

    public QuestManager() {
        quests = new HashMap<>();
    }

    /**
     * Registers a Quest with the MMORPG.
     */
    public void registerQuest(@NotNull Quest quest) {
        String name = quest.getName();
        if (quests.containsKey(name)) {
            throw new IllegalArgumentException("Attempted to register a quest "
                    + "with a name that is already registered: " + name);
        }
        quest.init();
        quests.put(quest.getName(), quest);
    }

    /**
     * Returns the registered Quest with the given name, or null if none
     * exists.
     */
    public Quest getQuest(@NotNull String name) {
        return quests.get(name);
    }

    public Collection<Quest> getQuests() {
        return quests.values();
    }
}
