package com.mcquest.server.features;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.event.PlayerCharacterLoginEvent;
import com.mcquest.core.feature.Feature;
import com.mcquest.core.object.ObjectSpawner;
import com.mcquest.server.constants.Instances;
import com.mcquest.server.constants.Quests;
import com.mcquest.server.npc.Broodmother;
import com.mcquest.server.npc.UndeadKnight;
import net.minestom.server.coordinate.Pos;

public class TestFeature implements Feature {
    @Override
    public void hook(Mmorpg mmorpg) {
        mmorpg.getGlobalEventHandler().addListener(PlayerCharacterLoginEvent.class, event -> {
            PlayerCharacter pc = event.getPlayerCharacter();
            pc.setMaxHealth(253);
            pc.setHealth(pc.getMaxHealth());
            pc.setMaxMana(107);
            pc.setMana(pc.getMaxMana());
            pc.grantExperiencePoints(6000);
            Quests.ARACHNOPHOBIA.start(pc);
            Quests.ARACHNOPHOBIA.getObjective(0).addProgress(pc, 14);
            Quests.FANGS_AND_FUMES.start(pc);
            Quests.FANGS_AND_FUMES.getObjective(0).addProgress(pc, 3);
            Quests.FANGS_AND_FUMES.getObjective(1).addProgress(pc, 1);
        });
//        mmorpg.getObjectManager().add(ObjectSpawner.of(
//                Instances.ELADRADOR,
//                new Pos(0, 69, 0),
//                Broodmother::new
//        ));
    }
}
