package net.mcquest.server.features;

import com.google.common.base.Predicates;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.PlayerCharacterLoginEvent;
import net.mcquest.core.feature.Feature;
import net.mcquest.core.loot.ItemPoolEntry;
import net.mcquest.core.loot.LootChest;
import net.mcquest.core.loot.LootTable;
import net.mcquest.core.loot.Pool;
import net.mcquest.core.object.ObjectManager;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.core.physics.Collider;
import net.mcquest.core.physics.Triggers;
import net.mcquest.core.quest.QuestManager;
import net.mcquest.core.quest.QuestMarker;
import net.mcquest.core.quest.QuestMarkerIcon;
import net.mcquest.core.util.Debug;
import net.mcquest.server.constants.*;
import net.mcquest.server.npc.Dreadfang;
import net.mcquest.server.npc.Grimrot;
import net.mcquest.server.npc.GoblinMinion;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.player.PlayerStartSneakingEvent;

import java.time.Duration;

public class KingsDeathRow implements Feature {
    private Mmorpg mmorpg;

    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        createKingsDeathRowBounds();
        npcs();
        lootChests();
        createQuestMarkers();
    }

private void createKingsDeathRowBounds() {
        Collider bounds = new Collider(
                Instances.ELADRADOR,
                new Pos(2976, 73, 3632),
                new Pos(3020, 90, 3695)
        );
        Debug.showCollider(bounds);
        bounds.onCollisionEnter(Triggers.playerCharacter(this::handleEnterKingsDeathRow));
        bounds.onCollisionExit(Triggers.playerCharacter(this::handleExitKingsDeathRow));
        mmorpg.getPhysicsManager().addCollider(bounds);
    }

    private void npcs() {
        goblinMinions();
        dreadfang();
        grimrot();
    }

    private void goblinMinions() {
        Pos[] positions = {
                // new Pos(3044, 99.0, 3654, 175.2f, 17.5f)
        };

        ObjectManager objectManager = mmorpg.getObjectManager();
        for (Pos position : positions) {
            objectManager.add(ObjectSpawner.of(Instances.ELADRADOR, position, GoblinMinion::new));
        }
    }

    private void dreadfang() {
        mmorpg.getObjectManager().add(ObjectSpawner.of(
                Instances.ELADRADOR,
                new Pos(2990, 78, 3659),
                Dreadfang::new
        ));
    }

    private void grimrot() {
        mmorpg.getObjectManager().add(ObjectSpawner.of(
                Instances.ELADRADOR,
                new Pos(2990, 78, 3659),
                Grimrot::new
        ));
    }

    private void lootChests() {
        ObjectManager objectManager = mmorpg.getObjectManager();

        objectManager.add(ObjectSpawner.of(
                Instances.ELADRADOR,
                new Pos(2987, 75, 3691, 180f, 0.0f),
                this::createLootChest1
        ));
    }

    private LootChest createLootChest1(Mmorpg mmorpg, ObjectSpawner spawner) {
        LootChest lootChest = new LootChest(mmorpg, spawner, LootTable.builder()
                .pool(Pool.builder()
                        .entry(ItemPoolEntry.builder(Items.ADVENTURERS_SWORD).build())
                        .build())
                .build());
        lootChest.setRespawnDuration(Duration.ofSeconds(60));
        return lootChest;
    }

    private void createQuestMarkers() {
        QuestManager questManager = mmorpg.getQuestManager();

        QuestMarker dreadfangMarker = questManager.createQuestMarker(
                Instances.ELADRADOR,
                new Pos(2993, 78, 3665),
                Quests.DREADFANGS_REVENGE,
                QuestMarkerIcon.OBJECTIVE,
                Predicates.or(
                        Quests.DREADFANGS_REVENGE.getObjective(0)::isInProgress,
                        Quests.DREADFANGS_REVENGE.getObjective(1)::isInProgress
                )
        );
        Maps.ELADRADOR.addQuestMarker(dreadfangMarker);
        System.out.println(dreadfangMarker);

        // replace below with new quest giver
        /* QuestMarker guardThomasMarker = questManager.createQuestMarker(
                Instances.ELADRADOR,
                new Pos(3198, 117, 3646),
                Quests.DREADFANGS_REVENGE,
                QuestMarkerIcon.READY_TO_TURN_IN,
                Quests.DREADFANGS_REVENGE.getObjective(2)::isInProgress
        );
        Maps.ELADRADOR.addQuestMarker(guardThomasMarker); */
    }

    private void handleEnterKingsDeathRow(PlayerCharacter pc) {
        pc.setZone(Zones.KINGS_DEATH_ROW);
        pc.getMusicPlayer().setSong(Music.KINGS_DEATH_ROW);
    }

    private void handleExitKingsDeathRow(PlayerCharacter pc) {
        pc.setZone(Zones.PROWLWOOD);
        pc.getMusicPlayer().setSong(Music.WILDERNESS);
    }
}