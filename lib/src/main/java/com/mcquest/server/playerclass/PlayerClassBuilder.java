package com.mcquest.server.playerclass;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PlayerClassBuilder {
    final PlayerClassManager playerClassManager;
    final String name;
    final List<Skill> skills;
    Consumer<SkillTreeBuilder> skillTreeBuilderConsumer;

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

    public PlayerClassBuilder skillTree(Consumer<SkillTreeBuilder> consumer) {
        this.skillTreeBuilderConsumer = consumer;
        return this;
    }

    public PlayerClass build() {
        PlayerClass playerClass = new PlayerClass(this);
        playerClassManager.registerPlayerClass(playerClass);
        return playerClass;
    }
}
