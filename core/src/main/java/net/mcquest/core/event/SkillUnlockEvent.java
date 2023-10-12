package net.mcquest.core.event;

import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.playerclass.Skill;
import net.minestom.server.event.Event;

public class SkillUnlockEvent implements Event {
    private final PlayerCharacter pc;
    private final Skill skill;

    public SkillUnlockEvent(PlayerCharacter pc, Skill skill) {
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
