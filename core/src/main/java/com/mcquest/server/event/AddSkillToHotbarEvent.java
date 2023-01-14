package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.playerclass.ActiveSkill;
import net.minestom.server.event.Event;

public class AddSkillToHotbarEvent implements Event {
    private final PlayerCharacter pc;
    private final ActiveSkill skill;
    private final int slot;

    public AddSkillToHotbarEvent(PlayerCharacter pc, ActiveSkill skill, int slot) {
        this.pc = pc;
        this.skill = skill;
        this.slot = slot;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    public ActiveSkill getSkill() {
        return skill;
    }

    public int getSlot() {
        return slot;
    }
}

