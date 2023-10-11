package net.mcquest.core.event;

import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.playerclass.ActiveSkill;
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

