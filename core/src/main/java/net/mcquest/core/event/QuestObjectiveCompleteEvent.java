package net.mcquest.core.event;

import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.quest.QuestObjective;
import net.minestom.server.event.Event;

public class QuestObjectiveCompleteEvent implements Event {
    private final PlayerCharacter pc;
    private final QuestObjective objective;

    public QuestObjectiveCompleteEvent(PlayerCharacter pc, QuestObjective objective) {
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
