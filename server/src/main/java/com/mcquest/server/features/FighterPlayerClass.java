package com.mcquest.server.features;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.constants.FighterSkills;
import com.mcquest.server.event.PlayerCharacterUseActiveSkillEvent;
import com.mcquest.server.feature.Feature;

public class FighterPlayerClass implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        FighterSkills.BASH.onUse().subscribe(this::useBash);
        FighterSkills.SELF_HEAL.onUse().subscribe(this::useSelfHeal);
    }

    private void useBash(PlayerCharacterUseActiveSkillEvent event) {
    }

    private void useSelfHeal(PlayerCharacterUseActiveSkillEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        pc.heal(pc, 10.0);
    }
}
