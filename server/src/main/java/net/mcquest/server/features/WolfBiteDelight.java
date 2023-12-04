package net.mcquest.server.features;

import com.google.common.base.Predicates;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.core.quest.QuestManager;
import net.mcquest.core.quest.QuestMarker;
import net.mcquest.core.quest.QuestMarkerIcon;
import net.mcquest.server.constants.Instances;
import net.mcquest.server.constants.Items;
import net.mcquest.server.constants.Maps;
import net.mcquest.server.constants.Quests;
import net.mcquest.server.npc.ChefMarco;
import net.minestom.server.coordinate.Pos;

public class WolfBiteDelight implements Feature {
    private Mmorpg mmorpg;

    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        chefMarco();
        createQuestMarkers();
        Items.WOLF_FLANK.registerObjective(Quests.WOLF_BITE_DELIGHT.getObjective(0));
    }

    private void chefMarco() {
        mmorpg.getObjectManager().add(ObjectSpawner.of(
                Instances.ELADRADOR,
                new Pos(2057, 87, 2914),
                ChefMarco::new
        ));
    }

    private void createQuestMarkers() {
        QuestManager questManager = mmorpg.getQuestManager();

        QuestMarker startMarker = questManager.createQuestMarker(
                Instances.ELADRADOR,
                new Pos(2057, 87, 2914),
                Quests.WOLF_BITE_DELIGHT,
                QuestMarkerIcon.READY_TO_START,
                Predicates.and(
                        Quests.TUTORIAL::isComplete,
                        Quests.WOLF_BITE_DELIGHT::isNotStarted
                )
        );
        Maps.ELADRADOR.addQuestMarker(startMarker);

        QuestMarker turnInMarker = questManager.createQuestMarker(
                Instances.ELADRADOR,
                new Pos(2057, 87, 2914),
                Quests.WOLF_BITE_DELIGHT,
                QuestMarkerIcon.READY_TO_TURN_IN,
                Quests.WOLF_BITE_DELIGHT.getObjective(1)::isInProgress
        );
        Maps.ELADRADOR.addQuestMarker(turnInMarker);
    }
}
