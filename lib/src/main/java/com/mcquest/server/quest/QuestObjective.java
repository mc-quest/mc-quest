package com.mcquest.server.quest;

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
}
