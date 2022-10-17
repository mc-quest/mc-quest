package com.mcquest.server.playerclass;

import com.mcquest.server.character.PlayerCharacter;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerCharacterSkillManager {
    private final PlayerCharacter pc;
    private final Set<Skill> unlockedSkills;
    private final Map<ActiveSkill, Duration> cooldowns;

    public PlayerCharacterSkillManager(PlayerCharacter pc) {
        this.pc = pc;
        unlockedSkills = new HashSet<>();
        cooldowns = new HashMap<>();
    }

    public boolean isUnlocked(Skill skill) {
        return unlockedSkills.contains(skill);
    }

    public Duration getCooldown(ActiveSkill skill) {
        return cooldowns.get(skill);
    }
}
