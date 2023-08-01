package com.mcquest.core.quest;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.event.EventEmitter;
import com.mcquest.core.event.QuestObjectiveChangeProgressEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public final class QuestObjective {
    private final int index;
    private final String description;
    private final int goal;
    private final int[] prerequisites;
    private final EventEmitter<QuestObjectiveChangeProgressEvent> onProgress;
    Quest quest;

    QuestObjective(int index, String description, int goal, int[] prerequisites) {
        this.index = index;
        this.description = description;
        this.goal = goal;
        this.prerequisites = prerequisites;
        this.onProgress = new EventEmitter<>();
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

    public Collection<QuestObjective> getPrerequisites() {
        return Arrays.stream(prerequisites)
                .mapToObj(quest::getObjective)
                .collect(Collectors.toList());
    }

    public Quest getQuest() {
        return quest;
    }

    public EventEmitter<QuestObjectiveChangeProgressEvent> onProgress() {
        return onProgress;
    }

    public boolean isAccessible(PlayerCharacter pc) {
        return pc.getQuestTracker().isAvailable(this);
    }

    public int getProgress(PlayerCharacter pc) {
        return pc.getQuestTracker().getProgress(this);
    }

    public void addProgress(PlayerCharacter pc, int progress) {
        pc.getQuestTracker().addProgress(this, progress);
    }

    public void addProgress(PlayerCharacter pc) {
        addProgress(pc, 1);
    }

    public boolean isComplete(PlayerCharacter pc) {
        return pc.getQuestTracker().isComplete(this);
    }
}
