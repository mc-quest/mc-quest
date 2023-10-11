package net.mcquest.server.features;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.PlayerCharacterLoginEvent;
import net.mcquest.core.feature.Feature;
import net.mcquest.server.constants.Quests;

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
