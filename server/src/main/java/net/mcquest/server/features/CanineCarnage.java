package net.mcquest.server.features;

import com.google.common.base.Predicates;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.PlayerCharacter;
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
import net.mcquest.server.constants.*;
import net.mcquest.server.npc.DirePacklord;
import net.mcquest.server.npc.DireWolf;
import net.minestom.server.coordinate.Pos;

import java.time.Duration;

public class CanineCarnage implements Feature {
    private Mmorpg mmorpg;

    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        createDenBounds();
        npcs();
        lootChests();
        createQuestMarkers();
    }

    private void createDenBounds() {
        Collider bounds = new Collider(
                Instances.ELADRADOR,
                new Pos(2064, 61, 2711),
                new Pos(2130, 90, 2755)
        );
        bounds.onCollisionEnter(Triggers.playerCharacter(this::handleEnterDen));
        bounds.onCollisionExit(Triggers.playerCharacter(this::handleExitDen));
        mmorpg.getPhysicsManager().addCollider(bounds);
    }

    private void npcs() {
        direWolves();
        direPacklord();
    }

    private void direWolves() {
        Pos[] positions = {
                // Forest wolves
                new Pos(1989, 82, 2894),
                new Pos(1980, 78, 2921),
                new Pos(1964, 71, 2940),
                new Pos(1972, 70, 2975),
                new Pos(1964, 67, 2998),
                new Pos(1928, 61, 2989),
                new Pos(1925, 66, 2930),
                new Pos(1931, 65, 2891),
                new Pos(1940, 71, 2855),
                new Pos(1979, 88, 2847),
                new Pos(1978, 85, 2821),
                new Pos(1959, 73, 2808),
                new Pos(1947, 65, 2791),
                new Pos(1963, 68, 2762),
                new Pos(1997, 77, 2748),
                new Pos(2025, 74, 2757),
                new Pos(2051, 76, 2761),
                new Pos(2071, 83, 2732),
                new Pos(2066, 81, 2708),
                new Pos(2045, 71, 2693),
                new Pos(2024, 69, 2681),
                new Pos(2073, 70, 2682),
                new Pos(2104, 71, 2698),
                new Pos(2146, 73, 2726),
                new Pos(2152, 75, 2726),
                new Pos(2183, 71, 2727),
                new Pos(2201, 78, 2749),
                new Pos(2218, 87, 2757),
                new Pos(2199, 95, 2805),
                new Pos(2166, 95, 2809),
                new Pos(2169, 106, 2865),
                new Pos(2113, 104, 2845),
                new Pos(2065, 91, 2808),
                new Pos(2077, 81, 2795),
                new Pos(2127, 89, 2770),
                new Pos(2114, 89, 2791),
                new Pos(2124, 85, 2751),
                new Pos(2036, 80, 2789),
                new Pos(2065, 75, 2766),
                new Pos(2090, 77, 2774),
                // Den wolves
                new Pos(2101, 68, 2726),
                new Pos(2096, 68, 2715)
        };

        ObjectManager objectManager = mmorpg.getObjectManager();
        for (Pos position : positions) {
            objectManager.add(ObjectSpawner.of(Instances.ELADRADOR, position, DireWolf::new));
        }
    }

    private void direPacklord() {
        mmorpg.getObjectManager().add(ObjectSpawner.of(
                Instances.ELADRADOR,
                new Pos(2100, 68, 2721),
                DirePacklord::new
        ));
    }

    private void lootChests() {
        ObjectManager objectManager = mmorpg.getObjectManager();

        objectManager.add(ObjectSpawner.of(
                Instances.ELADRADOR,
                new Pos(2071, 65, 2717, -90, 0),
                this::createDenLootChest
        ));
    }

    private LootChest createDenLootChest(Mmorpg mmorpg, ObjectSpawner spawner) {
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

        QuestMarker wolfDenMarker = questManager.createQuestMarker(
                Instances.ELADRADOR,
                new Pos(2126, 87, 2756),
                Quests.CANINE_CARNAGE,
                QuestMarkerIcon.OBJECTIVE,
                Predicates.or(
                        Quests.CANINE_CARNAGE.getObjective(0)::isInProgress,
                        Quests.CANINE_CARNAGE.getObjective(1)::isInProgress
                )
        );
        Maps.ELADRADOR.addQuestMarker(wolfDenMarker);

        QuestMarker startQuestMarker = questManager.createQuestMarker(
                Instances.ELADRADOR,
                new Pos(2044, 84, 2937),
                Quests.CANINE_CARNAGE,
                QuestMarkerIcon.READY_TO_START,
                Predicates.and(
                        Quests.TUTORIAL::isComplete,
                        Quests.CANINE_CARNAGE::isNotStarted
                )
        );
        Maps.ELADRADOR.addQuestMarker(startQuestMarker);

        QuestMarker turnInMarker = questManager.createQuestMarker(
                Instances.ELADRADOR,
                new Pos(2044, 84, 2937),
                Quests.CANINE_CARNAGE,
                QuestMarkerIcon.READY_TO_TURN_IN,
                Quests.CANINE_CARNAGE.getObjective(2)::isInProgress
        );
        Maps.ELADRADOR.addQuestMarker(turnInMarker);
    }

    private void handleEnterDen(PlayerCharacter pc) {
        pc.setZone(Zones.PACKLORD_DEN);
        pc.getMusicPlayer().setSong(Music.WOLF_DEN);
    }

    private void handleExitDen(PlayerCharacter pc) {
        pc.setZone(Zones.PROWLWOOD);
        pc.getMusicPlayer().setSong(Music.WILDERNESS);
    }
}
