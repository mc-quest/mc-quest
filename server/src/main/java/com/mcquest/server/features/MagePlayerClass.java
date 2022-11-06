package com.mcquest.server.features;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.constants.MageSkills;
import com.mcquest.server.constants.PlayerClasses;
import com.mcquest.server.event.PlayerCharacterUseActiveSkillEvent;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.playerclass.Skill;
import net.minestom.server.event.GlobalEventHandler;

public class MagePlayerClass implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        eventHandler.addListener(PlayerCharacterUseActiveSkillEvent.class, this::handleUseSkill);
    }

    private void handleUseSkill(PlayerCharacterUseActiveSkillEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        if (pc.getPlayerClass() != PlayerClasses.MAGE) {
            return;
        }
        Skill skill = event.getSkill();
        switch (skill.getId()) {
            case MageSkills.FIREBALL:
                useFireball(pc);
                break;
            case MageSkills.ICE_BEAM:
                useIceBeam(pc);
                break;
        }
    }

    private void useFireball(PlayerCharacter pc) {
        // TODO
    }

    private void useIceBeam(PlayerCharacter pc) {
        // TODO
    }
}
