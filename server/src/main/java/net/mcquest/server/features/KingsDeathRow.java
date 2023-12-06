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
import net.mcquest.server.npc.GuardAnya;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.player.PlayerStartSneakingEvent;

import java.time.Duration;

public class KingsDeathRow implements Feature {  // ALL POSITIONS ARE PLACEHOLDERS
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
                new Pos(2056, 60, 3115),
                new Pos(2265, 131, 3417)
        );
        Debug.showCollider(bounds);
        bounds.onCollisionEnter(Triggers.playerCharacter(this::handleEnterKingsDeathRow));
        bounds.onCollisionExit(Triggers.playerCharacter(this::handleExitKingsDeathRow));
        mmorpg.getPhysicsManager().addCollider(bounds);
    }

    private void npcs() {
        goblinMinions();
        // dreadfang();
        grimrot();
        guardAnya();
    }

    private void goblinMinions() {
        Pos[] positions = {
                new Pos(2846, 78, 3181),
                new Pos(2171, 80, 3182),
                new Pos(2185, 84, 3192),
                new Pos(2130, 84, 3200),
                new Pos(2139, 84, 3203),
                new Pos(2137, 84, 3220),
                new Pos(2137, 84, 3250),
                new Pos(2147, 84, 3251),
                new Pos(2122, 85, 3264),
                new Pos(2125, 70, 3282),
                new Pos(2151, 69, 3293),
                new Pos(2140, 72, 3319),
                new Pos(2093, 74, 3322),
                new Pos(2119, 74, 3296),
                new Pos(2131, 71, 3272),
                new Pos(2151, 76, 3311),
                new Pos(2160, 74, 3330),
                new Pos(2183, 74, 3354),
                new Pos(2206, 106, 3380),
                new Pos(2196, 69, 3289),
                new Pos(2846, 78, 3181),
                new Pos(2171, 80, 3182),
                new Pos(2185, 84, 3192),
                new Pos(2130, 84, 3200),
                new Pos(2139, 84, 3203),
                new Pos(2137, 84, 3220),
                new Pos(2137, 84, 3250),
                new Pos(2147, 84, 3251),
                new Pos(2122, 85, 3264),
                // new Pos(2125, 70, 3282),
                // new Pos(2151, 69, 3293),
                // new Pos(2140, 72, 3319),
                // new Pos(2093, 74, 3322),
                // new Pos(2119, 74, 3296),
                // new Pos(2131, 71, 3272),
                // new Pos(2151, 76, 3311),
                // new Pos(2160, 74, 3330),
                // new Pos(2183, 74, 3354),
                // new Pos(2206, 106, 3380),
                // new Pos(2196, 69, 3289),
                // new Pos(2846, 78, 3181),
                // new Pos(2171, 80, 3182),
                // new Pos(2185, 84, 3192),
                // new Pos(2130, 84, 3200),
                // new Pos(2139, 84, 3203),
                // new Pos(2137, 84, 3220),
                // new Pos(2137, 84, 3250),
                // new Pos(2147, 84, 3251),
                // new Pos(2122, 85, 3264),
                // new Pos(2125, 70, 3282),
                // new Pos(2151, 69, 3293),
                // new Pos(2140, 72, 3319),
                // new Pos(2093, 74, 3322),
                // new Pos(2119, 74, 3296),
                // new Pos(2131, 71, 3272),
                // new Pos(2151, 76, 3311),
                // new Pos(2160, 74, 3330),
                // new Pos(2183, 74, 3354),
                // new Pos(2206, 106, 3380),
                // new Pos(2196, 69, 3289)
        };

        ObjectManager objectManager = mmorpg.getObjectManager();
        for (Pos position : positions) {
            objectManager.add(ObjectSpawner.of(Instances.ELADRADOR, position, GoblinMinion::new));
        }
    }

    private void dreadfang() {
        mmorpg.getObjectManager().add(ObjectSpawner.of(
                Instances.ELADRADOR,
                new Pos(2136, 84, 3257),
                Dreadfang::new
        ));
    }

    private void grimrot() {
        mmorpg.getObjectManager().add(ObjectSpawner.of(
                Instances.ELADRADOR,
                new Pos(2157, 78, 3324),
                Grimrot::new
        ));
    }

    private void guardAnya() {
        mmorpg.getObjectManager().add(ObjectSpawner.of(
                Instances.ELADRADOR,
                new Pos(2123, 80, 3124, 180, 0),
                GuardAnya::new
        ));
    }

    private void lootChests() {
        ObjectManager objectManager = mmorpg.getObjectManager();

        objectManager.add(ObjectSpawner.of(
                Instances.ELADRADOR,
                new Pos(2169, 75, 3339, 180f, 0.0f),
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
                new Pos(2136, 84, 3257),
                Quests.DREADFANGS_REVENGE,
                QuestMarkerIcon.OBJECTIVE,
                Predicates.or(
                        Quests.DREADFANGS_REVENGE.getObjective(0)::isInProgress
                        // Quests.DREADFANGS_REVENGE.getObjective(1)::isInProgress
                )
        );
        Maps.ELADRADOR.addQuestMarker(dreadfangMarker);
        System.out.println(dreadfangMarker);

        QuestMarker guardAnyaMarker = questManager.createQuestMarker(
                Instances.ELADRADOR,
                new Pos(2123, 80, 3124),
                Quests.DREADFANGS_REVENGE,
                QuestMarkerIcon.READY_TO_TURN_IN,
                Quests.DREADFANGS_REVENGE.getObjective(1)::isInProgress
        );
        Maps.ELADRADOR.addQuestMarker(guardAnyaMarker);
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