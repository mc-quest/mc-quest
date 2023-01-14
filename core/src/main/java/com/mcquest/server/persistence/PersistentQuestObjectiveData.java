package com.mcquest.server.persistence;

public class PersistentQuestObjectiveData {
    private final int questId;
    private final int[] objectiveProgress;

    PersistentQuestObjectiveData(int questId, int[] objectiveProgress) {
        this.questId = questId;
        this.objectiveProgress = objectiveProgress;
    }

    public int getQuestId() {
        return questId;
    }

    public int[] getObjectiveProgress() {
        return objectiveProgress;
    }
}
