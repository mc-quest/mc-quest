package com.mcquest.server.playerclass;

import java.util.ArrayList;
import java.util.List;

public class PlayerClassBuilder {
    private final PlayerClassManager playerClassManager;
    private final String name;
    private final List<Skill> skills;

    PlayerClassBuilder(PlayerClassManager playerClassmanager, String name) {
        this.playerClassManager = playerClassmanager;
        this.name = name;
        this.skills = new ArrayList<>();
    }

    public PlayerClassBuilder skill(String name, int level, String description) {
        Skill skill = new Skill(name, level, description);
        skills.add(skill);
        return this;
    }

    public PlayerClass build() {
        PlayerClass playerClass = new PlayerClass(name, skills.toArray(new Skill[0]));
        playerClassManager.registerPlayerClass(playerClass);
        return playerClass;
    }
}
