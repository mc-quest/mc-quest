package com.mcquest.core.event;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.quest.Quest;
import net.minestom.server.event.Event;

public class QuestCompleteEvent implements Event {
    private final PlayerCharacter pc;
    private final Quest quest;

    public QuestCompleteEvent(PlayerCharacter pc, Quest quest) {
        this.pc = pc;
        this.quest = quest;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    public Quest getQuest() {
        return quest;
    }
}
