package net.mcquest.core.playerclass;

import java.util.*;

/**
 * A PlayerClass represents the specialization of a PlayerCharacter.
 */
public final class PlayerClass {
    private final String id;
    private final String name;
    private final Map<String, Skill> skillsById;

    PlayerClass(Builder builder) {
        id = builder.id;
        name = builder.name;
        skillsById = new HashMap<>();
        for (Skill skill : builder.skills) {
            skill.playerClass = this;
            skillsById.put(skill.getId(), skill);
        }
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Skill getSkill(String id) {
        return skillsById.get(id);
    }

    public Collection<Skill> getSkills() {
        return Collections.unmodifiableCollection(skillsById.values());
    }

    public static Builder builder(String id, String name) {
        return new Builder(id, name);
    }

    public static class Builder {
        private final String id;
        private final String name;
        final Collection<Skill> skills;

        private Builder(String id, String name) {
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
