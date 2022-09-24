package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.playerclass.Skill;
import net.minestom.server.event.Event;

public class PlayerCharacterUpgradeSkillEvent implements Event {
    private final PlayerCharacter pc;
    private final Skill skill;
    private final int upgradeLevel;

    public PlayerCharacterUpgradeSkillEvent(PlayerCharacter pc, Skill skill, int upgradeLevel) {
        this.pc = pc;
        this.skill = skill;
        this.upgradeLevel = upgradeLevel;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    public Skill getSkill() {
        return skill;
    }

    public int getUpgradeLevel() {
        return upgradeLevel;
    }
}
