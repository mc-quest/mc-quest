package net.mcquest.core.event;

import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.playerclass.ActiveSkill;
import net.minestom.server.event.trait.CancellableEvent;

public class ActiveSkillUseEvent implements CancellableEvent {
    private final PlayerCharacter pc;
    private final ActiveSkill skill;
    private boolean cancelled;

    public ActiveSkillUseEvent(PlayerCharacter pc, ActiveSkill skill) {
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
