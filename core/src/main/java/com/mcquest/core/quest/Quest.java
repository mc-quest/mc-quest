package com.mcquest.core.quest;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.event.QuestCompleteEvent;
import com.mcquest.core.event.QuestStartEvent;
import com.mcquest.core.event.EventEmitter;

import java.util.ArrayList;
import java.util.List;

/**
 * A Quest represents a series of objectives for a player to complete.
 */
public final class Quest {
    private final int id;
    private final String name;
    private final int level;
    private final QuestObjective[] objectives;
    private final EventEmitter<QuestStartEvent> onStart;
    private final EventEmitter<QuestCompleteEvent> onComplete;

    Quest(Builder builder) {
        id = builder.id;
        name = builder.name;
        level = builder.level;
        objectives = builder.objectives.toArray(new QuestObjective[0]);
        onStart = new EventEmitter<>();
        onComplete = new EventEmitter<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public QuestObjective getObjective(int index) {
        return objectives[index];
    }

    public int getObjectiveCount() {
        return objectives.length;
    }

    public QuestStatus getStatus(PlayerCharacter pc) {
        return pc.getQuestTracker().getStatus(this);
    }

    public boolean compareStatus(PlayerCharacter pc, QuestStatus status) {
        return pc.getQuestTracker().compareStatus(this, status);
    }

    public void start(PlayerCharacter pc) {
        pc.getQuestTracker().startQuest(this);
    }

    public EventEmitter<QuestStartEvent> onStart() {
        return onStart;
    }

    public EventEmitter<QuestCompleteEvent> onComplete() {
        return onComplete;
    }

    public static Builder builder(int id, String name, int level) {
        return new Builder(id, name, level);
    }

    public static class Builder {
        final int id;
        final String name;
        final int level;
        final List<QuestObjective> objectives;

        private Builder(int id, String name, int level) {
            this.id = id;
            this.name = name;
            this.level = level;
            this.objectives = new ArrayList<>();
        }

        public Builder objective(String description, int goal) {
            objectives.add(new QuestObjective(objectives.size(), description, goal));
            return this;
        }

        public Quest build() {
            Quest quest = new Quest(this);
            for (QuestObjective objective : objectives) {
                objective.quest = quest;
            }
            return quest;
        }
    }
}
