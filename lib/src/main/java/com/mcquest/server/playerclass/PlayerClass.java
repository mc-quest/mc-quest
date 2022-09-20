package com.mcquest.server.playerclass;

/**
 * A PlayerClass represents the specialization of a PlayerCharacter.
 */
public final class PlayerClass {
    private final int id;
    private final String name;
    private final Skill[] skills;
    private final SkillTreeDecoration[] skillTreeDecorations;

    PlayerClass(PlayerClassBuilder builder) {
        id = builder.id;
        name = builder.name;
        skills = builder.skills.toArray(new Skill[0]);
        for (Skill skill : skills) {
            skill.playerClass = this;
        }
        skillTreeDecorations = builder.skillTreeDecorations
                .toArray(new SkillTreeDecoration[0]);
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Skill getSkill(int index) {
        return skills[index];
    }

    public int getSkillCount() {
        return skills.length;
    }

    public SkillTreeDecoration getSkillTreeDecoration(int index) {
        return skillTreeDecorations[index];
    }

    public int getSkillTreeDecorationCount() {
        return skillTreeDecorations.length;
    }
}
