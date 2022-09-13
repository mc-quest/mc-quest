package com.mcquest.server.quest;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerCharacterQuestManager {
    private final Set<Quest> completedQuests;
    private final Set<Quest> inProgressQuests;
    private final Map<QuestObjective, Integer> objectiveProgress;

    public PlayerCharacterQuestManager() {
        // TODO: populate these
        completedQuests = new HashSet<>();
        inProgressQuests = new HashSet<>();
        objectiveProgress = new HashMap<>();
    }

    public QuestStatus getStatus(Quest quest) {
        if (completedQuests.contains(quest)) {
            return QuestStatus.COMPLETED;
        }
        if (inProgressQuests.contains(quest)) {
            return QuestStatus.IN_PROGRESS;
        }
        return QuestStatus.NOT_STARTED;
    }

    public void startQuest(Quest quest) {
        if (inProgressQuests.contains(quest) || completedQuests.contains(quest)) {
            throw new IllegalArgumentException();
        }
        inProgressQuests.add(quest);
        for (int i = 0; i < quest.getObjectiveCount(); i++) {
            QuestObjective objective = quest.getObjective(i);
            objectiveProgress.put(objective, 0);
        }
    }

    public int getProgress(QuestObjective objective) {
        if (completedQuests.contains(objective)) {
            return objective.getGoal();
        }
        if (!inProgressQuests.contains(objective)) {
            return 0;
        }
        return objectiveProgress.get(objective);
    }

    public void setProgress(QuestObjective objective, int progress) {
        if (progress < 0 || progress > objective.getGoal()) {
            throw new IllegalArgumentException();
        }
        if (!objectiveProgress.containsKey(objective)) {
            throw new IllegalArgumentException();
        }

        if (progress == objective.getGoal()) {
            // Check for quest completion.
        }
    }
}
