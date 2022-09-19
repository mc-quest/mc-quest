package com.mcquest.server.playerclass;

/**
 * A PlayerClass represents the specialization of a PlayerCharacter.
 */
public final class PlayerClass {
    private final String name;
    private final Skill[] skills;
    private final SkillTree skillTree;

    PlayerClass(PlayerClassBuilder builder) {
        this.name = builder.name;
        this.skills = builder.skills.toArray(new Skill[0]);
        SkillTreeBuilder skillTreeBuilder = new SkillTreeBuilder(this);
        if (builder.skillTreeBuilderConsumer != null) {
            builder.skillTreeBuilderConsumer.accept(skillTreeBuilder);
        }
        this.skillTree = skillTreeBuilder.build();
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
