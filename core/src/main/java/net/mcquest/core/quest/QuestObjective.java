package net.mcquest.core.quest;

import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.EventEmitter;
import net.mcquest.core.event.QuestObjectiveChangeProgressEvent;
import net.mcquest.core.event.QuestObjectiveCompleteEvent;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public final class QuestObjective {
    private final int index;
    private final String description;
    private final int goal;
    private final int[] prerequisites;
    private final EventEmitter<QuestObjectiveChangeProgressEvent> onProgress;
    private final EventEmitter<QuestObjectiveCompleteEvent> onComplete;
    Quest quest;

    QuestObjective(int index, String description, int goal, int[] prerequisites) {
        this.index = index;
        this.description = description;
        this.goal = goal;
        this.prerequisites = prerequisites;
        onProgress = new EventEmitter<>();
        onComplete = new EventEmitter<>();
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

    public EventEmitter<QuestObjectiveCompleteEvent> onComplete() {
        return onComplete;
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

    public boolean isInProgress(PlayerCharacter pc) {
        return isAccessible(pc) && !isComplete(pc);
    }

    public boolean isComplete(PlayerCharacter pc) {
        return pc.getQuestTracker().isComplete(this);
    }
}
