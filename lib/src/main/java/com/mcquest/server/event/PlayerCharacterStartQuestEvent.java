package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.quest.Quest;
import net.minestom.server.event.Event;

public class PlayerCharacterStartQuestEvent implements Event {
    private final PlayerCharacter pc;
    private final Quest quest;

    public PlayerCharacterStartQuestEvent(PlayerCharacter pc, Quest quest) {
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
