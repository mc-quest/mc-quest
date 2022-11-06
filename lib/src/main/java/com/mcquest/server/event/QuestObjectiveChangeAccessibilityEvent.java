package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.quest.QuestObjective;
import net.minestom.server.event.Event;

public class QuestObjectiveChangeAccessibilityEvent implements Event {
    private final PlayerCharacter pc;
    private final QuestObjective objective;

    public QuestObjectiveChangeAccessibilityEvent(PlayerCharacter pc, QuestObjective objective) {
        this.pc = pc;
        this.objective = objective;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    public QuestObjective getObjective() {
        return objective;
    }
}
