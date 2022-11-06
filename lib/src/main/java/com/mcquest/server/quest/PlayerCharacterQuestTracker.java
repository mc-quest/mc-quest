package com.mcquest.server.quest;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.event.PlayerCharacterCompleteQuestEvent;
import com.mcquest.server.event.PlayerCharacterStartQuestEvent;
import com.mcquest.server.util.MathUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

public class PlayerCharacterQuestTracker {
    private final PlayerCharacter pc;
    /**
     * Internally, progress of -1 indicates that objective is inaccessible.
     */
    private final Map<Quest, int[]> objectiveProgress;
    private final Set<Quest> completedQuests;

    @ApiStatus.Internal
    public PlayerCharacterQuestTracker(PlayerCharacter pc, Map<Quest, int[]> objectiveProgress,
                                       Set<Quest> completedQuests) {
        this.pc = pc;
        this.objectiveProgress = objectiveProgress;
        this.completedQuests = completedQuests;
    }

    public Set<Quest> getInProgressQuests() {
        return Collections.unmodifiableSet(objectiveProgress.keySet());
    }

    public Set<Quest> getCompletedQuests() {
        return Collections.unmodifiableSet(completedQuests);
    }

    public QuestStatus getStatus(Quest quest) {
        if (completedQuests.contains(quest)) {
            return QuestStatus.COMPLETED;
        }
        if (objectiveProgress.containsKey(quest)) {
            return QuestStatus.IN_PROGRESS;
        }
        return QuestStatus.NOT_STARTED;
    }

    public boolean compareStatus(Quest quest, QuestStatus status) {
        return getStatus(quest) == status;
    }

    public void startQuest(Quest quest) {
        if (objectiveProgress.containsKey(quest) || completedQuests.contains(quest)) {
            return;
        }
        int objectiveCount = quest.getObjectiveCount();
        int[] initialProgress = new int[objectiveCount];
        for (int i = 0; i < objectiveCount; i++) {
            initialProgress[i] = -1;
        }
        objectiveProgress.put(quest, initialProgress);
        pc.sendMessage(Component.text("Quest started: " + quest.getName(), NamedTextColor.GREEN));
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.call(new PlayerCharacterStartQuestEvent(pc, quest));
    }

    public int getProgress(QuestObjective objective) {
        Quest quest = objective.getQuest();
        if (completedQuests.contains(quest)) {
            return objective.getGoal();
        }
        if (!objectiveProgress.containsKey(quest)) {
            return 0;
        }
        int progress = objectiveProgress.get(quest)[objective.getIndex()];
        if (progress == -1) {
            return 0;
        }
        return progress;
    }

    public void setProgress(QuestObjective objective, int progress) {
        Quest quest = objective.getQuest();
        if (!objectiveProgress.containsKey(quest)) {
            return;
        }
        int[] currentProgress = objectiveProgress.get(quest);
        int objectiveIndex = objective.getIndex();
        if (currentProgress[objectiveIndex] == -1) {
            // Inaccessible.
            return;
        }
        progress = MathUtility.clamp(progress, 0, objective.getGoal());
        currentProgress[objectiveIndex] = progress;
        if (progress == objective.getGoal()) {
            checkForCompletion(quest);
        }
    }

    public void addProgress(QuestObjective objective, int progress) {
        setProgress(objective, getProgress(objective) + progress);
    }

    public boolean isComplete(QuestObjective objective) {
        return getProgress(objective) == objective.getGoal();
    }

    public void complete(QuestObjective objective) {
        setProgress(objective, objective.getGoal());
    }

    private void checkForCompletion(Quest quest) {
        if (allObjectivesComplete(quest)) {
            complete(quest);
        }
    }

    private boolean allObjectivesComplete(Quest quest) {
        int[] progress = objectiveProgress.get(quest);
        for (int i = 0; i < quest.getObjectiveCount(); i++) {
            QuestObjective objective = quest.getObjective(i);
            if (progress[i] != objective.getGoal()) {
                return false;
            }
        }
        return true;
    }

    private void complete(Quest quest) {
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        objectiveProgress.remove(quest);
        completedQuests.add(quest);
        pc.sendMessage(Component.text("Quest completed: " + quest.getName(), NamedTextColor.GREEN));
        eventHandler.call(new PlayerCharacterCompleteQuestEvent(pc, quest));
    }

    /**
     * Returns whether the objective is accessible to the player character. An
     * objective is accessible if it has been made accessible and the quest has
     * not yet been completed.
     */
    public boolean isAccessible(QuestObjective objective) {
        Quest quest = objective.getQuest();
        int index = objective.getIndex();
        int[] progress = objectiveProgress.get(quest);
        if (progress == null) {
            return false;
        }
        return progress[index] != -1;
    }

    public void setAccessible(QuestObjective objective, boolean accessible) {
        Quest quest = objective.getQuest();
        int index = objective.getIndex();
        objectiveProgress.get(quest)[index] = 0;
    }

    private List<Component> sidebarText() {
        List<Component> text = new ArrayList<>();
        for (Quest quest : objectiveProgress.keySet()) {
            text.add(Component.text(quest.getName(), NamedTextColor.YELLOW));
            for (int i = 0; i < quest.getObjectiveCount(); i++) {
                QuestObjective objective = quest.getObjective(i);
                text.add(Component.text(objective.getDescription(), NamedTextColor.WHITE));
            }
        }
        return text;
    }
}
