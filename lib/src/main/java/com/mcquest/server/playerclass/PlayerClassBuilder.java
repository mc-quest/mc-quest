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

    public PlayerClassBuilder skill() {
        // TODO
        Skill skill = null;
        skills.add(skill);
        return this;
    }

    public PlayerClass build() {
        PlayerClass playerClass = new PlayerClass(name, skills.toArray(new Skill[0]));
        playerClassManager.registerPlayerClass(playerClass);
        return playerClass;
    }
}
