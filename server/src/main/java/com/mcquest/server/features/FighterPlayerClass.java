package com.mcquest.server.features;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.constants.PlayerClasses;
import com.mcquest.server.event.PlayerCharacterUseActiveSkillEvent;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.playerclass.Skill;
import net.minestom.server.event.GlobalEventHandler;

public class FighterPlayerClass implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        eventHandler.addListener(PlayerCharacterUseActiveSkillEvent.class, this::handleUseSkill);
    }

    private void handleUseSkill(PlayerCharacterUseActiveSkillEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (pc.getPlayerClass() != PlayerClasses.FIGHTER) {
            return;
        }
        Skill skill = event.getSkill();
    }

    private void useBash(PlayerCharacter pc) {

    }

    private void useSelfHeal(PlayerCharacter pc) {
        pc.heal(pc, 10.0);
    }
}
