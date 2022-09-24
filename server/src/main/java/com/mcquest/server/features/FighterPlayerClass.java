package com.mcquest.server.features;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.constants.PlayerClasses;
import com.mcquest.server.constants.FighterSkills;
import com.mcquest.server.event.PlayerCharacterUseSkillEvent;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.playerclass.Skill;
import net.minestom.server.event.GlobalEventHandler;

public class FighterPlayerClass implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        eventHandler.addListener(PlayerCharacterUseSkillEvent.class, this::handleUseSkill);
    }

    private void handleUseSkill(PlayerCharacterUseSkillEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (pc.getPlayerClass().getId() != PlayerClasses.FIGHTER) {
            return;
        }
        Skill skill = event.getSkill();
        switch (skill.getId()) {
            case FighterSkills.BASH:
                useBash(pc);
                break;
            case FighterSkills.SELF_HEAL:
                useSelfHeal(pc);
                break;
        }
    }

    private void useBash(PlayerCharacter pc) {

    }

    private void useSelfHeal(PlayerCharacter pc) {
        pc.heal(pc, 10.0);
    }
}
