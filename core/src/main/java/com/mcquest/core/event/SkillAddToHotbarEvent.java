package com.mcquest.core.event;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.playerclass.ActiveSkill;
import net.minestom.server.event.Event;

public class SkillAddToHotbarEvent implements Event {
    private final PlayerCharacter pc;
    private final ActiveSkill skill;
    private final int slot;

    public SkillAddToHotbarEvent(PlayerCharacter pc, ActiveSkill skill, int slot) {
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

