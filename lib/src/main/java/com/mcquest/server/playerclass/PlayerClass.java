package com.mcquest.server.playerclass;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A PlayerClass represents the specialization of a PlayerCharacter.
 */
public final class PlayerClass {
    private final int id;
    private final String name;
    private final Map<Integer, Skill> skillsById;
    private final SkillTreeDecoration[] skillTreeDecorations;

    PlayerClass(PlayerClassBuilder builder) {
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
}
