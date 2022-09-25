package com.mcquest.server.quest;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.event.PlayerCharacterStartQuestEvent;
import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;

import java.util.*;

public class PlayerCharacterQuestTracker {
    private final PlayerCharacter pc;
    private final Set<Quest> completedQuests;
    private final Set<Quest> inProgressQuests;
    private final Map<QuestObjective, Integer> objectiveProgress;

    public PlayerCharacterQuestTracker(PlayerCharacter pc) {
        this.pc = pc;
        // TODO: populate these
        completedQuests = new HashSet<>();
        inProgressQuests = new HashSet<>();
        objectiveProgress = new HashMap<>();
    }

    public Set<Quest> getInProgressQuests() {
        return Collections.unmodifiableSet(inProgressQuests);
    }

    public Set<Quest> getCompletedQuests() {
        return Collections.unmodifiableSet(completedQuests);
    }

    public QuestStatus getStatus(Quest quest) {
        if (completedQuests.contains(quest)) {
            return QuestStatus.COMPLETED;
        }
        if (inProgressQuests.contains(quest)) {
            return QuestStatus.IN_PROGRESS;
        }
        return QuestStatus.NOT_STARTED;
    }

    public boolean compareStatus(Quest quest, QuestStatus status) {
        return getStatus(quest) == status;
    }

    public void startQuest(Quest quest) {
        if (inProgressQuests.contains(quest) || completedQuests.contains(quest)) {
            return;
        }
        inProgressQuests.add(quest);
        for (int i = 0; i < quest.getObjectiveCount(); i++) {
            QuestObjective objective = quest.getObjective(i);
            objectiveProgress.put(objective, 0);
        }
        pc.sendMessage(Component.text("Quest started: " + quest.getName()));
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.call(new PlayerCharacterStartQuestEvent(pc, quest));
    }

    public int getProgress(QuestObjective objective) {
        Quest quest = objective.getQuest();
        if (completedQuests.contains(quest)) {
            return objective.getGoal();
        }
        if (!inProgressQuests.contains(quest)) {
            return 0;
        }
        return objectiveProgress.get(objective);
    }

    public void setProgress(QuestObjective objective, int progress) {
        if (progress < 0 || progress > objective.getGoal()) {
            throw new IllegalArgumentException();
        }
        if (!objectiveProgress.containsKey(objective)) {
            return;
        }

        if (progress == objective.getGoal()) {
            // TODO: Check for quest completion.
        }
    }
}
