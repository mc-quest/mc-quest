package com.mcquest.server.playerclass;

/**
 * A PlayerClass represents the specialization of a PlayerCharacter.
 */
public final class PlayerClass {
    private final String name;
    private final Skill[] skills;

    /**
     * Constructs a PlayerClass with the given name and Skills.
     */
    public PlayerClass(String name, Skill[] skills) {
        this.name = name;
        this.skills = skills.clone();
    }

    /**
     * Returns the name of this PlayerClass.
     */
    public String getName() {
        return name;
    }

    public Skill getSkill(int index) {
        return skills[index];
    }

    public int getSkillCount() {
        return skills.length;
    }
}
