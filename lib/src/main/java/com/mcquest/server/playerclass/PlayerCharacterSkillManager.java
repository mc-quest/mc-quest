package com.mcquest.server.playerclass;

import com.mcquest.server.character.PlayerCharacter;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class PlayerCharacterSkillManager {
    private final PlayerCharacter pc;
    private final Map<ActiveSkill, Duration> cooldowns;

    public PlayerCharacterSkillManager(PlayerCharacter pc) {
        this.pc = pc;
        cooldowns = new HashMap<>();
    }

    public Duration getCooldown(ActiveSkill skill) {
        return cooldowns.get(skill);
    }
}
