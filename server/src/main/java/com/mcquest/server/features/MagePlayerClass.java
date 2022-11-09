package com.mcquest.server.features;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.constants.MageSkills;
import com.mcquest.server.event.PlayerCharacterUseActiveSkillEvent;
import com.mcquest.server.feature.Feature;

public class MagePlayerClass implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        MageSkills.FIREBALL.onUse().subscribe(this::useFireball);
    }

    private void useFireball(PlayerCharacterUseActiveSkillEvent event) {
    }
}
