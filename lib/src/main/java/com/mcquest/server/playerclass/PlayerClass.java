package com.mcquest.server.playerclass;

import net.minestom.server.item.Material;

import java.util.*;

/**
 * A PlayerClass represents the specialization of a PlayerCharacter.
 */
public final class PlayerClass {
    private final int id;
    private final String name;
    private final Map<Integer, Skill> skillsById;
    private final SkillTreeDecoration[] skillTreeDecorations;

    PlayerClass(Builder builder) {
        id = builder.id;
        name = builder.name;
        skillsById = new HashMap<>();
        for (Skill skill : builder.skills) {
            skill.playerClass = this;
            skillsById.put(skill.getId(), skill);
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

    public Skill getSkill(int id) {
        return skillsById.get(id);
    }

    public Collection<Skill> getSkills() {
        return Collections.unmodifiableCollection(skillsById.values());
    }

    public SkillTreeDecoration getSkillTreeDecoration(int index) {
        return skillTreeDecorations[index];
    }

    public int getSkillTreeDecorationCount() {
        return skillTreeDecorations.length;
    }

    public static Builder builder(int id, String name) {
        return new Builder(id, name);
    }

    public static class Builder {
        final int id;
        final String name;
        final List<Skill> skills;
        final List<SkillTreeDecoration> skillTreeDecorations;

        private Builder(int id, String name) {
            this.id = id;
            this.name = name;
            this.skills = new ArrayList<>();
            this.skillTreeDecorations = new ArrayList<>();
        }

        public Builder activeSkill(int id, String name, int level, Material icon,
                                   String description, double manaCost,
                                   int skillTreeRow, int skillTreeColumn) {
            ActiveSkill skill = new ActiveSkill(id, name, level, icon, description,
                    skillTreeRow, skillTreeColumn, manaCost);
            skills.add(skill);
            return this;
        }

        public Builder passiveSkill(int id, String name, int level, Material icon,
                                    String description, int skillTreeRow,
                                    int skillTreeColumn) {
            PassiveSkill skill = new PassiveSkill(id, name, level, icon, description,
                    skillTreeRow, skillTreeColumn);
            skills.add(skill);
            return this;
        }

        public Builder skillTreeDecoration(Material icon, int row, int column) {
            SkillTreeDecoration decoration = new SkillTreeDecoration(icon, row, column);
            skillTreeDecorations.add(decoration);
            return this;
        }

        public PlayerClass build() {
            return new PlayerClass(this);
        }
    }
}
