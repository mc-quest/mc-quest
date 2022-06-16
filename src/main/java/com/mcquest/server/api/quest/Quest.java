package com.mcquest.server.api.quest;

import com.mcquest.server.api.character.PlayerCharacter;
import net.kyori.adventure.text.Component;

/**
 * A Quest represents a series of objectives for a player to complete.
 */
public final class Quest {
    private final String name;
    private final int level;
    private final QuestObjective[] objectives;

    /**
     * Constructs a Quest with the given name, level, and Objectives.
     */
    public Quest(String name, int level, QuestObjective[] objectives) {
        this.name = name;
        this.level = level;
        this.objectives = objectives.clone();
    }

    /**
     * Returns the name of this Quest.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the level of this Quest.
     */
    public int getLevel() {
        return level;
    }

    /**
     * Returns the QuestObjective with the given index in this Quest.
     */
    public QuestObjective getObjective(int index) {
        return objectives[index];
    }

    /**
     * Returns the number of objectives in this Quest.
     */
    public int getObjectiveCount() {
        return objectives.length;
    }

    /**
     * Returns the status of this Quest for the given PlayerCharacter.
     */
    public QuestStatus getStatus(PlayerCharacter pc) {
        PlayerCharacterQuestManager pcQuestManager = pc.getQuestManager();
        return pcQuestManager.getQuestStatus(this);
    }

    /**
     * Returns true if the PlayerCharacter's status in this Quest matches the
     * given status, false otherwise.
     */
    public boolean compareStatus(PlayerCharacter pc, QuestStatus status) {
        return getStatus(pc) == status;
    }

    /**
     * Starts this quest for the given PlayerCharacter.
     */
    public void start(PlayerCharacter pc) {
        PlayerCharacterQuestManager pcQuestManager = pc.getQuestManager();
        pcQuestManager.startQuest(this);
        pc.sendMessage(Component.text("Started " + name));
    }

    void init() {
        for (int i = 0; i < objectives.length; i++) {
            QuestObjective objective = objectives[i];
            objective.index = i;
            objective.quest = this;
        }
    }
}
