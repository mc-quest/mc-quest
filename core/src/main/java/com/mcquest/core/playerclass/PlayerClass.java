package com.mcquest.core.playerclass;

import com.mcquest.core.asset.Asset;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;

/**
 * A PlayerClass represents the specialization of a PlayerCharacter.
 */
public final class PlayerClass {
    private final int id;
    private final String name;
    private final Map<Integer, Skill> skillsById;

    PlayerClass(Builder builder) {
        id = builder.id;
        name = builder.name;
        skillsById = new HashMap<>();
        for (Skill skill : builder.skills) {
            skill.playerClass = this;
            skillsById.put(skill.getId(), skill);
        }
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

    public static Builder builder(int id, String name) {
        return new Builder(id, name);
    }

    public static class Builder {
        final int id;
        final String name;
        final List<Skill> skills;

        private Builder(int id, String name) {
            this.id = id;
            this.name = name;
            this.skills = new ArrayList<>();
        }

        public Builder activeSkill(int id, String name, int level, @Nullable Integer prerequisiteId,
                                   Asset icon, String description, int skillTreeRow,
                                   int skillTreeColumn, double manaCost, Duration cooldown) {
            ActiveSkill skill = new ActiveSkill(id, name, level, prerequisiteId, icon,
                    description, skillTreeRow, skillTreeColumn, manaCost, cooldown);
            skills.add(skill);
            return this;
        }

        public Builder passiveSkill(int id, String name, int level, @Nullable Integer prerequisiteId,
                                    Asset icon, String description, int skillTreeRow, int skillTreeColumn) {
            PassiveSkill skill = new PassiveSkill(id, name, level, prerequisiteId, icon,
                    description, skillTreeRow, skillTreeColumn);
            skills.add(skill);
            return this;
        }

        public PlayerClass build() {
            return new PlayerClass(this);
        }
    }
}
