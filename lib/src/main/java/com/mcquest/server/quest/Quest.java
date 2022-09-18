package com.mcquest.server.quest;

import com.mcquest.server.character.PlayerCharacter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

/**
 * A Quest represents a series of objectives for a player to complete.
 */
public final class Quest {
    private final int id;
    private final String name;
    private final int level;
    private final QuestObjective[] objectives;

    Quest(QuestBuilder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.level = builder.level;
        this.objectives = builder.objectives.toArray(new QuestObjective[0]);
    }

    public int getId() {
        return id;
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
}
