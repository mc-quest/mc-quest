package com.mcquest.server.playerclass;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.event.PlayerCharacterUnlockSkillEvent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.event.GlobalEventHandler;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerCharacterSkillManager {
    private final PlayerCharacter pc;
    private int skillPoints;
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

    public int getSkillPoints() {
        return skillPoints;
    }

    @ApiStatus.Internal
    public void grantSkillPoint() {
        skillPoints++;
    }

    @ApiStatus.Internal
    public void unlockSkill(Skill skill) {
        skillPoints--;
        unlockedSkills.add(skill);
        PlayerCharacterUnlockSkillEvent event = new PlayerCharacterUnlockSkillEvent(pc, skill);
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.call(event);
    }
}
