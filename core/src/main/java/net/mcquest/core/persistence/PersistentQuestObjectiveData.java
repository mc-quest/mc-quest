package net.mcquest.core.persistence;

public class PersistentQuestObjectiveData {
    private final String questId;
    private final int[] objectiveProgress;

    PersistentQuestObjectiveData(String questId, int[] objectiveProgress) {
        this.questId = questId;
        this.objectiveProgress = objectiveProgress;
    }

    public String getQuestId() {
        return questId;
    }

    public int[] getObjectiveProgress() {
        return objectiveProgress;
    }
}
