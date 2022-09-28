package com.mcquest.server.quest;

import com.mcquest.server.character.PlayerCharacter;

public final class QuestObjective {
    private final int index;
    private final String description;
    private final int goal;
    Quest quest;

    QuestObjective(int index, String description, int goal) {
        this.index = index;
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

    public boolean isAccessible(PlayerCharacter pc) {
        return pc.getQuestTracker().isAccessible(this);
    }

    public void setAccessible(PlayerCharacter pc, boolean accessible) {
        pc.getQuestTracker().setAccessible(this, accessible);
    }

    public int getProgress(PlayerCharacter pc) {
        return pc.getQuestTracker().getProgress(this);
    }

    public void setProgress(PlayerCharacter pc, int progress) {
        pc.getQuestTracker().setProgress(this, progress);
    }

    public void addProgress(PlayerCharacter pc, int progress) {
        pc.getQuestTracker().addProgress(this, progress);
    }

    public void complete(PlayerCharacter pc) {
        pc.getQuestTracker().complete(this);
    }
}
