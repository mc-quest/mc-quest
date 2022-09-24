package com.mcquest.server.quest;

import java.util.ArrayList;
import java.util.List;

public class QuestBuilder {
    final QuestManager questManager;
    final int id;
    final String name;
    final int level;
    final List<QuestObjective> objectives;

    QuestBuilder(QuestManager questManager, int id, String name, int level) {
        this.questManager = questManager;
        this.id = id;
        this.name = name;
        this.level = level;
        this.objectives = new ArrayList<>();
    }

    public QuestBuilder objective(String description, int goal) {
        objectives.add(new QuestObjective(objectives.size(), description, goal));
        return this;
    }

    public Quest build() {
        Quest quest = new Quest(this);
        for (QuestObjective objective : objectives) {
            objective.quest = quest;
        }
        questManager.registerQuest(quest);
        return quest;
    }
}
