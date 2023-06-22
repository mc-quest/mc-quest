package com.mcquest.core.event;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.quest.QuestObjective;
import net.minestom.server.event.Event;

public class QuestObjectiveChangeAccessibilityEvent implements Event {
    private final PlayerCharacter pc;
    private final QuestObjective objective;
    private final boolean accessible;

    public QuestObjectiveChangeAccessibilityEvent(PlayerCharacter pc, QuestObjective objective,
                                                  boolean accessible) {
        this.pc = pc;
        this.objective = objective;
        this.accessible = accessible;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    public QuestObjective getObjective() {
        return objective;
    }

    public boolean isAccessible() {
        return accessible;
    }
}
