package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.playerclass.Skill;
import net.minestom.server.event.Event;

public class PlayerCharacterUseSkillEvent implements Event {
    private final PlayerCharacter pc;
    private final Skill skill;

    public PlayerCharacterUseSkillEvent(PlayerCharacter pc, Skill skill) {
        this.pc = pc;
        this.skill = skill;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    public Skill getSkill() {
        return skill;
    }
}
