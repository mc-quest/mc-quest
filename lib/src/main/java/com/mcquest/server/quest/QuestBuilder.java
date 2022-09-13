package com.mcquest.server.quest;

import java.util.ArrayList;
import java.util.List;

public class QuestBuilder {
    private final QuestManager questManager;
    private final int id;
    private final String name;
    private final int level;
    private final List<QuestObjective> objectives;

    public QuestBuilder(QuestManager questManager, int id, String name, int level) {
        this.questManager = questManager;
        this.id = id;
        this.name = name;
        this.level = level;
        this.objectives = new ArrayList<>();
    }

    public QuestBuilder objective(String description, int objective) {
        objectives.add(new QuestObjective(objectives.size(), description, objective));
        return this;
    }

    public Quest build() {
        Quest quest = new Quest(id, name, level, objectives.toArray(new QuestObjective[0]));
        for (QuestObjective objective : objectives) {
            objective.quest = quest;
        }
        questManager.registerQuest(quest);
        return quest;
    }
}
