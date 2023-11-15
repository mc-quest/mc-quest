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
import net.mcquest.core.util.Debug;
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
                new Pos(2976, 73, 3632),
                new Pos(3020, 90, 3695)
        );
        Debug.showCollider(bounds);
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
                // forest wolves
                new Pos(3044, 99.0, 3654, 175.2f, 17.5f),
                new Pos(3046, 103.0, 3601, 56.0f, 8.5f),
                new Pos(3099, 105.0, 3630, 56.0f, 8.5f),
                new Pos(3092, 105.0, 3685, 56.0f, 8.5f),
                new Pos(3152, 108.0, 3619, 56.0f, 8.5f),
                new Pos(3044, 99.0, 3654, 175.2f, 17.5f),
                new Pos(3046, 103.0, 3601, 56.0f, 8.5f),
                new Pos(3099, 105.0, 3630, 56.0f, 8.5f),
                new Pos(3092, 105.0, 3685, 56.0f, 8.5f),
                new Pos(3152, 108.0, 3619, 56.0f, 8.5f),
                new Pos(3075, 114.0, 3615, 56.0f, 8.5f),
                new Pos(3059, 114.0, 3513, 56.0f, 8.5f),
                new Pos(3053, 97.0, 3678, 56.0f, 8.5f),
                new Pos(3127, 98.0, 3827, 56.0f, 8.5f),
                new Pos(3195, 112.0, 3869, 56.0f, 8.5f),
                new Pos(3075, 114.0, 3615, 56.0f, 8.5f),
                new Pos(3059, 114.0, 3513, 56.0f, 8.5f),
                new Pos(3053, 97.0, 3678, 56.0f, 8.5f),
                new Pos(3127, 98.0, 3827, 56.0f, 8.5f),
                new Pos(3195, 112.0, 3869, 56.0f, 8.5f),
                new Pos(3185, 112.0, 3691, 56.0f, 8.5f),
                new Pos(3037, 99, 3662),
                new Pos(3013, 102, 3606),

                // den wolves
                new Pos(2990, 78, 3652),
                new Pos(2997, 78, 3661),
                new Pos(3012, 85, 3641),
        };

        ObjectManager objectManager = mmorpg.getObjectManager();
        for (Pos position : positions) {
            objectManager.add(ObjectSpawner.of(Instances.ELADRADOR, position, DireWolf::new));
        }
    }

    private void direPacklord() {
        mmorpg.getObjectManager().add(ObjectSpawner.of(
                Instances.ELADRADOR,
                new Pos(2990, 78, 3659),
                DirePacklord::new
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

        QuestMarker wolfDenMarker = questManager.createQuestMarker(
                Instances.ELADRADOR,
                new Pos(2993, 78, 3665),
                Quests.CANINE_CARNAGE,
                QuestMarkerIcon.OBJECTIVE,
                Predicates.or(
                        Quests.CANINE_CARNAGE.getObjective(0)::isInProgress,
                        Quests.CANINE_CARNAGE.getObjective(1)::isInProgress
                )
        );
        Maps.ELADRADOR.addQuestMarker(wolfDenMarker);
        System.out.println(wolfDenMarker);

        // to show quest marker
        QuestMarker canineCarnageQuestMarker = questManager.createQuestMarker(
                Instances.ELADRADOR,
                new Pos(3198, 117, 3646),
                Quests.CANINE_CARNAGE,
                QuestMarkerIcon.READY_TO_START,
                Predicates.and(
                        Quests.TUTORIAL::isComplete,
                        Quests.CANINE_CARNAGE::isNotStarted
                )
        );
        Maps.ELADRADOR.addQuestMarker(canineCarnageQuestMarker);

        QuestMarker guardThomasMarker = questManager.createQuestMarker(
                Instances.ELADRADOR,
                new Pos(3198, 117, 3646),
                Quests.CANINE_CARNAGE,
                QuestMarkerIcon.READY_TO_TURN_IN,
                Quests.CANINE_CARNAGE.getObjective(2)::isInProgress
        );
        Maps.ELADRADOR.addQuestMarker(guardThomasMarker);
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
