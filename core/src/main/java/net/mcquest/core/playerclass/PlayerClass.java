package net.mcquest.core.playerclass;

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
        private final int id;
        private final String name;
        final Collection<Skill> skills;

        private Builder(int id, String name) {
            this.id = id;
            this.name = name;
            this.skills = new ArrayList<>();
        }

        public ActiveSkill.IdStep activeSkill() {
            return new ActiveSkill.Builder(this);
        }

        public PassiveSkill.IdStep passiveSkill() {
            return new PassiveSkill.Builder(this);
        }

        public PlayerClass build() {
            return new PlayerClass(this);
        }
    }
}
