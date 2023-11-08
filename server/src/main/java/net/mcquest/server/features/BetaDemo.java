package net.mcquest.server.features;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.event.PlayerCharacterLoginEvent;
import net.mcquest.core.feature.Feature;

// TODO: DELETE ME LATER. I'M JUST HERE FOR THE DEMO.
public class BetaDemo implements Feature {
    @Override
    public void hook(Mmorpg mmorpg) {
        mmorpg.getGlobalEventHandler().addListener(PlayerCharacterLoginEvent.class, event -> {
            for (int i = 0; i < 5; i++) {
                event.getPlayerCharacter().getSkillManager().grantSkillPoint();
            }
        });
    }
}
