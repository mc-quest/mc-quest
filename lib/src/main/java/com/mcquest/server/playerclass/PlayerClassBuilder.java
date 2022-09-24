package com.mcquest.server.playerclass;

import net.minestom.server.item.Material;

import java.util.ArrayList;
import java.util.List;

public class PlayerClassBuilder {
    final PlayerClassManager playerClassManager;
    final int id;
    final String name;
    final List<Skill> skills;
    final List<SkillTreeDecoration> skillTreeDecorations;

    PlayerClassBuilder(PlayerClassManager playerClassmanager, int id, String name) {
        this.playerClassManager = playerClassmanager;
        this.id = id;
        this.name = name;
        this.skills = new ArrayList<>();
        this.skillTreeDecorations = new ArrayList<>();
    }

    public PlayerClassBuilder activeSkill(int id, String name, int level, Material icon,
                                          String description, double manaCost,
                                          int skillTreeRow, int skillTreeColumn) {
        ActiveSkill skill = new ActiveSkill(id, name, level, icon, description,
                skillTreeRow, skillTreeColumn, manaCost);
        skills.add(skill);
        return this;
    }

    public PlayerClassBuilder passiveSkill(int id, String name, int level, Material icon,
                                           String description, int skillTreeRow,
                                           int skillTreeColumn) {
        PassiveSkill skill = new PassiveSkill(id, name, level, icon, description,
                skillTreeRow, skillTreeColumn);
        skills.add(skill);
        return this;
    }

    public PlayerClassBuilder skillTreeDecoration(Material icon, int row, int column) {
        SkillTreeDecoration decoration = new SkillTreeDecoration(icon, row, column);
        skillTreeDecorations.add(decoration);
        return this;
    }

    public PlayerClass build() {
        PlayerClass playerClass = new PlayerClass(this);
        playerClassManager.registerPlayerClass(playerClass);
        return playerClass;
    }
}
