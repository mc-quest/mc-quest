package com.mcquest.server.quest;

import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ApiStatus.Internal
public class PlayerCharacterQuestManager {
    private final Map<Quest, int[]> data;
    private final Set<Quest> completedQuests;

    public PlayerCharacterQuestManager() {
        data = new HashMap<>();
        // TODO: populate completed quests.
        completedQuests = new HashSet<>();
    }

    QuestStatus getQuestStatus(Quest quest) {
        if (data.containsKey(quest)) {
            return QuestStatus.IN_PROGRESS;
        }
        if (completedQuests.contains(quest)) {
            return QuestStatus.COMPLETED;
        }
        return QuestStatus.NOT_STARTED;
    }

    void startQuest(Quest quest) {
        data.put(quest, new int[quest.getObjectiveCount()]);
    }

    int getObjectiveProgress(QuestObjective objective) {
        return data.get(objective.getQuest())[objective.getIndex()];
    }

    void setObjectiveProgress(QuestObjective objective, int progress) {
        data.get(objective.getQuest())[objective.getIndex()] = progress;
    }
}
