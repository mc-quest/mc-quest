package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.playerclass.ActiveSkill;
import net.minestom.server.event.Event;

public class PlayerCharacterUseActiveSkillEvent implements Event {
    private final PlayerCharacter pc;
    private final ActiveSkill skill;

    public PlayerCharacterUseActiveSkillEvent(PlayerCharacter pc, ActiveSkill skill) {
        this.pc = pc;
        this.skill = skill;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    public ActiveSkill getSkill() {
        return skill;
    }
}
