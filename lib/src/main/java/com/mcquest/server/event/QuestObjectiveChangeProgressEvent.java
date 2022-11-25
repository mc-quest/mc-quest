package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.quest.QuestObjective;
import net.minestom.server.event.Event;

public class QuestObjectiveChangeProgressEvent implements Event {
    private final PlayerCharacter pc;
    private final QuestObjective objective;
    private final int formerProgress;
    private final int newProgress;

    public QuestObjectiveChangeProgressEvent(PlayerCharacter pc, QuestObjective objective,
                                             int formerProgress, int newProgress) {
        this.pc = pc;
        this.objective = objective;
        this.formerProgress = formerProgress;
        this.newProgress = newProgress;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    public QuestObjective getObjective() {
        return objective;
    }

    public int getFormerProgress() {
        return formerProgress;
    }

    public int getNewProgress() {
        return newProgress;
    }

    public boolean objectiveIsComplete() {
        return newProgress == objective.getGoal();
    }
}
