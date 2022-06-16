package com.mcquest.server.quest;

import com.mcquest.server.character.PlayerCharacter;

public final class QuestObjective {
    private final String description;
    private final int goal;
    transient int index;
    transient Quest quest;

    public QuestObjective(String description, int goal) {
        this.description = description;
        this.goal = goal;
    }

    public String getDescription() {
        return description;
    }

    public int getGoal() {
        return goal;
    }

    public int getIndex() {
        return index;
    }

    public Quest getQuest() {
        return quest;
    }

    public int getProgress(PlayerCharacter pc) {
        PlayerCharacterQuestManager pcQuestManager = pc.getQuestManager();
        return pcQuestManager.getObjectiveProgress(this);
    }

    public void setProgress(PlayerCharacter pc, int progress) {
        PlayerCharacterQuestManager pcQuestManager = pc.getQuestManager();
        // TODO: What if player hasn't started quest?
        pcQuestManager.setObjectiveProgress(this, progress);
    }
}
