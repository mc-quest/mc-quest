package com.mcquest.server.playerclass;

public class SkillTreeBuilder {
    private final PlayerClass playerClass;

    SkillTreeBuilder(PlayerClass playerClass) {
        this.playerClass = playerClass;
    }

    SkillTree build() {
        return new SkillTree(playerClass, this);
    }
}
