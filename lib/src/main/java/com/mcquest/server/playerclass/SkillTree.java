package com.mcquest.server.playerclass;

import com.mcquest.server.character.PlayerCharacter;

public class SkillTree {
    private final PlayerClass playerClass;

    public SkillTree(PlayerClass playerClass) {
        this.playerClass = playerClass;
    }

    public void open(PlayerCharacter pc) {
        // TODO
    }

    private Skill skillAt(int row, int column) {
        for (int i = 0; i < playerClass.getSkillCount(); i++) {
            Skill skill = playerClass.getSkill(i);
            if (skill.getSkillTreeRow() == row && skill.getSkillTreeColumn() == column) {
                return skill;
            }
        }
        return null;
    }
}
