package com.mcquest.server.event;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.playerclass.ActiveSkill;
import net.minestom.server.event.trait.CancellableEvent;

public class PlayerCharacterUseActiveSkillEvent implements CancellableEvent {
    private final PlayerCharacter pc;
    private final ActiveSkill skill;
    private boolean cancelled;

    public PlayerCharacterUseActiveSkillEvent(PlayerCharacter pc, ActiveSkill skill) {
        this.pc = pc;
        this.skill = skill;
    }

    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    public ActiveSkill getSkill() {
        return skill;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
