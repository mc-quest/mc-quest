package net.mcquest.server.features;


import com.google.common.base.Predicates;
import net.kyori.adventure.text.Component;
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
import net.mcquest.core.physics.PhysicsManager;
import net.mcquest.core.physics.Triggers;
import net.mcquest.core.util.Debug;
import net.mcquest.server.constants.Quests;
import net.mcquest.server.npc.AlphaDireWolf;
import net.mcquest.server.npc.GuardThomas;
import net.mcquest.server.npc.DireWolf;
import net.mcquest.core.quest.*;
import net.mcquest.core.event.*;
import net.mcquest.server.constants.*;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.event.GlobalEventHandler;

import java.time.Duration;

public class CanineCarnage {

    private Mmorpg mmorpg;

    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        createWolfDenBounds();
        npcs();
        lootChests();
        createQuestMarkers();
        Quests.CANINE_CARNAGE.getObjective(3).onComplete().subscribe(this::handleSlayAlphaWolfObjectiveComplete);
    }

    private void createWolfDenBounds() {
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();
        Collider wolfDenBounds = new Collider(
                Instances.ELADRADOR,
                new Pos(3020, 92, 3636),
                new Pos(3006, 82, 3642)
        );
        wolfDenBounds.onCollisionEnter(Triggers.playerCharacter(this::handleEnterWolfDen));
        physicsManager.addCollider(wolfDenBounds);
    }

    private void npcs() {
        direWolves();
        alphaDireWolf();
    }

    private void direWolves() {
        Pos[] positions = {
                // forest wolves (20)
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

                // den wolves (5)
                new Pos(2995, 78.0, 3670, 56.0f, 8.5f),
                new Pos(2987, 78.0, 3670, -101.2f, 2.2f),
                new Pos(2991, 79.0, 3674, -101.2f, 2.2f),
                new Pos(3006, 82.0, 3642, -101.2f, 2.2f),
                new Pos(3020, 92.0, 3636, 175.2f, 17.5f)
        };

        ObjectManager objectManager = mmorpg.getObjectManager();
        for (Pos position : positions) {
            objectManager.add(ObjectSpawner.of(Instances.ELADRADOR, position, DireWolf::new));
        }
    }

    private void alphaDireWolf() {
        mmorpg.getObjectManager().add(ObjectSpawner.of(
                Instances.ELADRADOR,
                new Pos(2993, 78, 3665),
                AlphaDireWolf::new
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
                        Quests.CANINE_CARNAGE.getObjective(1)::isInProgress,
                        Quests.CANINE_CARNAGE.getObjective(2)::isInProgress
                )
        );
        Maps.ELADRADOR.addQuestMarker(wolfDenMarker);

        QuestMarker guardThomasMarker = questManager.createQuestMarker(
                Instances.ELADRADOR,
                new Pos(2871, 86, 3206),
                Quests.CANINE_CARNAGE,
                QuestMarkerIcon.READY_TO_TURN_IN,
                Quests.CANINE_CARNAGE.getObjective(3)::isInProgress
        );
        Maps.ELADRADOR.addQuestMarker(guardThomasMarker);
    }

    private boolean canineCarnageObjectiveActive(PlayerCharacter pc, int objectiveIndex) {
        QuestObjective objective = Quests.CANINE_CARNAGE.getObjective(objectiveIndex);
        return objective.isAccessible(pc) && !objective.isComplete(pc);
    }

    private void handleEnterWolfDen(PlayerCharacter pc) {
        if (!canineCarnageObjectiveActive(pc, 1)) {
            return;
        }
        Quests.CANINE_CARNAGE.getObjective(1).addProgress(pc);
        pc.sendMessage(Component.text("Welcome to the wolf den..."));
    }

    private void handleSlayAlphaWolfObjectiveComplete(QuestObjectiveCompleteEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        pc.sendMessage(Component.text("Impressive! Speak with Guard Thomas to claim your reward!"));
    }
}