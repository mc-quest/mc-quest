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
import net.mcquest.core.physics.PhysicsManager;
import net.mcquest.core.physics.Triggers;
import net.mcquest.core.quest.QuestManager;
import net.mcquest.core.quest.QuestMarker;
import net.mcquest.core.quest.QuestMarkerIcon;
import net.mcquest.core.util.Debug;
import net.mcquest.server.npc.*;
import net.mcquest.server.constants.*;
import net.minestom.server.coordinate.Pos;

import java.time.Duration;

public class BroodmotherLair implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        portals();
        npcs();
        createQuestMarkers();
        lootChests();
        // teleporters to speed run quests
        //instantTransmission();
    }

    private void portals() {
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();

        // entrance to broodmother lair
        Collider enter = new Collider(Instances.ELADRADOR, new Pos(2860, 74, 3610), new Pos(2867, 78, 3610));
        enter.onCollisionEnter(Triggers.playerCharacter(this::enter));
        physicsManager.addCollider(enter);
        Debug.showCollider(enter);

        Collider exit = new Collider(Instances.BROODMOTHER_LAIR, new Pos(135, 121, 198), new Pos(138, 124, 198));
        exit.onCollisionEnter(Triggers.playerCharacter(this::exitMain));
        physicsManager.addCollider(exit);
        Debug.showCollider(exit);

        Collider bossExit = new Collider(Instances.BROODMOTHER_LAIR, new Pos(83, 5, 0), new Pos(86, 8, 0));
        bossExit.onCollisionEnter(Triggers.playerCharacter(this::exitBoss));
        physicsManager.addCollider(bossExit);
        Debug.showCollider(bossExit);
    }

    private void enter(PlayerCharacter pc) {
        Pos to = new Pos(136, 120, 195, 180, 0);
        pc.setInstance(Instances.BROODMOTHER_LAIR, to);
        pc.setZone(Zones.BROODMOTHER_LAIR);
        pc.getMusicPlayer().setSong(Music.BROODMOTHER_LAIR);
        pc.setRespawnPoint(Instances.BROODMOTHER_LAIR, to);
        pc.getMapViewer().setMap(Maps.ELADRADOR);

        if (Quests.ITSY_BITSY_SPIDER.getObjective(1).isInProgress(pc)) {
            Quests.ITSY_BITSY_SPIDER.getObjective(1).addProgress(pc);
        }
    }

    // front entrance - exit
    private void exitMain(PlayerCharacter pc) {
        exit(pc, new Pos(2865, 73, 3607, 180, 0));
    }

    // end of dungeon (after boss) exit
    private void exitBoss(PlayerCharacter pc) {
        exit(pc, new Pos(2853, 75, 3624));
    }

    private void exit(PlayerCharacter pc, Pos to) {
        pc.setInstance(Instances.ELADRADOR, to);
        pc.setZone(Zones.ASHEN_TANGLE);
        pc.getMusicPlayer().setSong(Music.WILDERNESS);
        pc.setRespawnPoint(Instances.ELADRADOR, to);
        pc.getMapViewer().setMap(Maps.ELADRADOR);

        if (Quests.NOT_SO_ITSY_BITSY.getObjective(1).isInProgress(pc)) {
            Quests.NOT_SO_ITSY_BITSY.getObjective(1).addProgress(pc);
        }
    }

    private void npcs() {
        captainSeraphina();
        lieutenantOrion();
        outsideFishermen();
        fishermen();
        spiders();
        broodlingEggs();
        broodlingEggClusters();
        broodmother();
    }

    private void captainSeraphina() {
        Pos position = new Pos(2784, 84, 2955);
        ObjectSpawner spawner = ObjectSpawner.of(Instances.ELADRADOR, position, CaptainSeraphina::new);
        mmorpg.getObjectManager().add(spawner);
    }

    private void lieutenantOrion() {
        Pos position = new Pos(59.5, 68.0, 194.5, -145.0f, 0.0f);
        ObjectSpawner spawner = ObjectSpawner.of(Instances.BROODMOTHER_LAIR, position, LieutenantOrion::new);
        mmorpg.getObjectManager().add(spawner);
    }

    private void outsideFishermen() {
        Pos position = new Pos(2868, 73, 3603, 180, 0);
        ObjectSpawner spawner = ObjectSpawner.of(Instances.ELADRADOR, position, OutsideFishermen::new);
        mmorpg.getObjectManager().add(spawner);
    }

    private void fishermen() {
        Pos[] positions = {
                new Pos(58, 67.0, 190.0, 175.2f, 17.5f),
                new Pos(64.4, 69.0, 189.3, 56.0f, 8.5f),
                new Pos(65.3, 70.0, 194.9, -101.2f, 2.2f)
        };

        ObjectManager objectManager = mmorpg.getObjectManager();
        for (Pos position : positions) {
            objectManager.add(ObjectSpawner.of(Instances.BROODMOTHER_LAIR, position, Fishermen::new));
        }
    }

    private void spiders() {
        Pos[] positions = {
                new Pos(140.6, 119.0, 158.5, -1.3f, 15.7f),
                new Pos(150.0, 119.0, 156.8, 32.4f, 7.0f),
                new Pos(146.3, 119.0, 152.4, 110.2f, 9.9f),
                new Pos(166.6, 115.0, 138.8, -22.8f, 12.3f),
                new Pos(175.0, 113.0, 137.0, 126.3f, 13.9f),
                new Pos(177.9, 111.0, 130.7, 37.1f, -2.6f),
                new Pos(186.5, 112.0, 140.8, 148.5f, 11.1f),
                new Pos(193.6, 112.0, 135.5, -145.5f, 15.4f),
                new Pos(124.6, 110.0, 130.1, -39.6f, 1.6f),
                new Pos(131.2, 113.0, 137.3, 28.4f, 2.7f),
                new Pos(114.0, 103.0, 120.8, 153.9f, 4.7f),
                new Pos(111.2, 101.0, 115.1, 22.4f, 17.4f),
                new Pos(104.1, 101.0, 119.6, -66.4f, 8.0f),
                new Pos(101.4, 101.0, 126.3, -134.2f, 2.9f),
                new Pos(95.8, 97.0, 135.9, 119.7f, 21.8f),
                new Pos(79.1, 90.0, 146.7, -64.5f, 14.1f),
                new Pos(85.2, 91.0, 149.6, 77.1f, 22.7f),
                new Pos(82.2, 90.0, 153.1, 154.7f, 6.2f),
                new Pos(91.0, 83.0, 169.7, 29.6f, 28.1f),
                new Pos(90.2, 78.0, 185.7, 170.4f, -15.6f),
                new Pos(86.5, 76.0, 196.1, 102.2f, 21.8f),
                new Pos(83.0, 76.0, 192.8, 36.2f, 18.2f),
                new Pos(77.6, 74.0, 199.6, 127.8f, 23.0f),
                new Pos(72.2, 73.0, 195.1, 64.1f, 22.7f),
                new Pos(51.7, 63.0, 177.0, -44.8f, -6.6f),
                new Pos(45.9, 63.0, 176.3, -139.8f, 10.7f),
                new Pos(47.4, 62.9, 170.0, 38.3f, 25.8f),
                new Pos(38.1, 59.0, 170.8, -178.0f, 14.7f),
                new Pos(34.6, 58.0, 162.5, -19.0f, 6.2f),
                new Pos(21.3, 45.8, 177.4, 151.2f, -3.0f),
                new Pos(15.4, 44.0, 180.1, -150.9f, -8.8f),
                new Pos(32.3, 38.0, 191.4, -70.8f, 7.1f),
                new Pos(39.3, 38.0, 197.9, 96.0f, 2.1f),
                new Pos(31.7, 38.0, 203.4, -64.3f, 7.8f),
                new Pos(24.6, 38.0, 201.7, 166.9f, -6.9f),
                new Pos(47.3, 32.0, 215.2, 141.5f, -8.2f),
                new Pos(36.8, 34.9, 180.6, -40.8f, 17.0f),
                new Pos(42.0, 35.0, 181.2, 45.1f, 2.1f),
                new Pos(42.4, 30.0, 172.8, 23.7f, -16.0f),
                new Pos(50.7, 26.0, 164.9, -77.4f, 21.9f),
                new Pos(58.1, 26.0, 165.6, -142.3f, 20.1f),
                // new Pos(74.4, 24.0, 158.3, -109.5f, 19.5f),
                new Pos(80.7, 21.0, 150.9, 132.6f, 19.7f)
        };

        ObjectManager objectManager = mmorpg.getObjectManager();
        for (Pos position : positions) {
            objectManager.add(ObjectSpawner.of(Instances.BROODMOTHER_LAIR, position, Spider::new));
        }
    }

    private void broodlingEggs() {
        Pos[] positions = {
                // TODO: add individual egg coordinates later
                new Pos(76, 17, 130)
        };

        ObjectManager objectManager = mmorpg.getObjectManager();
        for (Pos position : positions) {
            objectManager.add(ObjectSpawner.of(Instances.BROODMOTHER_LAIR, position, SpiderEgg::new));
        }
    }

    private void broodlingEggClusters() {
        Pos[] positions = {
                // from entrance to Lieutenant Orion
                new Pos(66, 70, 189), new Pos(68, 72, 198), new Pos(80, 75, 201),
                new Pos(87, 77, 200), new Pos(84, 76, 192), new Pos(91, 78, 185),
                new Pos(87, 81, 174), new Pos(86, 84, 166), new Pos(84, 88, 159),
                new Pos(78, 90, 151), new Pos(86, 93, 141), new Pos(94, 98, 131),
                new Pos(97, 101, 124), new Pos(115, 102, 120), new Pos(121, 108, 126),
                new Pos(129, 112, 135), new Pos(136, 118, 148), new Pos(144, 199, 154),
                new Pos(144, 119, 158), new Pos(149, 119, 157), new Pos(147, 119, 154),
                new Pos(186, 111, 137), new Pos(190, 112, 138), new Pos(191, 112, 135),
                new Pos(191, 111, 128), new Pos(190, 111, 128), new Pos(186, 110, 128),
                new Pos(182, 111, 133), new Pos(178, 111, 132), new Pos(175, 113, 137),
                new Pos(168, 116, 143), new Pos(143, 118, 171), new Pos(137, 117, 174),
                new Pos(141, 116, 184), new Pos(134, 116, 185),
                // from Orion to Broodmother
                new Pos(53, 66, 182), new Pos(45, 62, 171), new Pos(33, 57, 166),
                new Pos(17, 46, 175), new Pos(27, 37, 197), new Pos(31, 38, 201),
                new Pos(33, 38, 196), new Pos(40, 38, 205), new Pos(48, 32, 216),
                new Pos(56, 30, 216), new Pos(58, 30, 208), new Pos(62, 30, 216),
                new Pos(55, 30, 211), new Pos(37, 38, 189), new Pos(38, 35, 182),
                new Pos(52, 26, 165), new Pos(75, 24, 161), new Pos(79, 18, 134),
                // Broodmother boss area
                new Pos(68, 7, 82), new Pos(54, 6, 74), new Pos(73, 7, 71),
                new Pos(79, 5, 88), new Pos(74, 7, 102), new Pos(56, 7, 95),
                new Pos(49, 6, 105), new Pos(58, 5, 84), new Pos(73, 6, 92),
                new Pos(65, 5, 104), new Pos(61, 7, 92), new Pos(82, 5, 73),
                new Pos(88, 6, 77), new Pos(94, 6, 80), new Pos(90, 8, 69),
                new Pos(68, 7, 59), new Pos(77, 7, 98), new Pos(73, 6, 94),
                new Pos(65, 8, 89), new Pos(62, 6, 82), new Pos(72, 6, 77),
                new Pos(79, 7, 80), new Pos(88, 7, 82), new Pos(89, 7, 87),
                new Pos(100, 8, 75), new Pos(97, 8, 56), new Pos(104, 8, 59),
                new Pos(99, 8, 42), new Pos(109, 6, 49)
        };

        ObjectManager objectManager = mmorpg.getObjectManager();
        for (Pos position : positions) {
            objectManager.add(ObjectSpawner.of(Instances.BROODMOTHER_LAIR, position, SpiderEggCluster::new));
        }
    }

    private void broodmother() {
        mmorpg.getObjectManager().add(ObjectSpawner.of(
                Instances.BROODMOTHER_LAIR,
                new Pos(73, 6, 92),
                Broodmother::new
        ));
    }

    private void createQuestMarkers() {
        QuestManager questManager = mmorpg.getQuestManager();

        QuestMarker startMarker = questManager.createQuestMarker(
                Instances.ELADRADOR,
                new Pos(2784, 84, 2955),
                Quests.ITSY_BITSY_SPIDER,
                QuestMarkerIcon.READY_TO_START,
                // may need to add more conditions
                // TODO: add pre-reqs accordingly
                Predicates.and(
                        // to speed up play test
                        //Quests.TUTORIAL::isComplete,
                        Quests.ITSY_BITSY_SPIDER::isNotStarted
                )
        );
        Maps.ELADRADOR.addQuestMarker(startMarker);

        QuestMarker turnInMarker = questManager.createQuestMarker(
                Instances.ELADRADOR,
                new Pos(2784, 84, 2955),
                Quests.NOT_SO_ITSY_BITSY,
                QuestMarkerIcon.READY_TO_TURN_IN,
                Quests.NOT_SO_ITSY_BITSY.getObjective(3)::isInProgress
        );
        Maps.ELADRADOR.addQuestMarker(turnInMarker);

        QuestMarker dungeonMarker = questManager.createQuestMarker(
                Instances.ELADRADOR,
                new Pos(2863, 73, 3607),
                Quests.ITSY_BITSY_SPIDER,
                QuestMarkerIcon.OBJECTIVE,
                Predicates.or(
                        Quests.ITSY_BITSY_SPIDER.getObjective(0)::isInProgress,
                        Quests.ITSY_BITSY_SPIDER.getObjective(1)::isInProgress
                )
        );
        Maps.ELADRADOR.addQuestMarker(dungeonMarker);

        QuestMarker outsideFishermenMarker = questManager.createQuestMarker(
                Instances.ELADRADOR,
                new Pos(2863, 73, 3607),
                Quests.NOT_SO_ITSY_BITSY,
                QuestMarkerIcon.OBJECTIVE,
                Quests.NOT_SO_ITSY_BITSY.getObjective(2)::isInProgress
        );
        Maps.ELADRADOR.addQuestMarker(outsideFishermenMarker);

        QuestMarker bossExitMarker = questManager.createQuestMarker(
                Instances.BROODMOTHER_LAIR,
                new Pos(83, 5, 0),
                Quests.NOT_SO_ITSY_BITSY,
                QuestMarkerIcon.OBJECTIVE,
                Quests.NOT_SO_ITSY_BITSY.getObjective(1)::isInProgress
        );
        Maps.ELADRADOR.addQuestMarker(bossExitMarker);

        QuestMarker lieutenantOrionMarker = questManager.createQuestMarker(
                Instances.BROODMOTHER_LAIR,
                new Pos(59.5, 68, 194.5),
                Quests.ITSY_BITSY_SPIDER,
                QuestMarkerIcon.READY_TO_TURN_IN,
                Quests.ITSY_BITSY_SPIDER.getObjective(2)::isInProgress
        );
        Maps.ELADRADOR.addQuestMarker(lieutenantOrionMarker);

        QuestMarker broodmotherMarker = questManager.createQuestMarker(
                Instances.BROODMOTHER_LAIR,
                new Pos(73, 6, 92),
                Quests.NOT_SO_ITSY_BITSY,
                QuestMarkerIcon.OBJECTIVE,
                Quests.NOT_SO_ITSY_BITSY.getObjective(0)::isInProgress
        );
        Maps.ELADRADOR.addQuestMarker(broodmotherMarker);
    }

    private void lootChests() {
        ObjectManager objectManager = mmorpg.getObjectManager();

        objectManager.add(ObjectSpawner.of(
                Instances.BROODMOTHER_LAIR,
                new Pos(189, 113, 132, 90, 0),
                this::createLootChest1
        ));

        objectManager.add(ObjectSpawner.of(
                Instances.BROODMOTHER_LAIR,
                new Pos(62, 29, 211.5, 90, 0),
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

    // creates portals to decrease play-testing time (removes traveling times)
    private void instantTransmission() {
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();

        // from spawning point to Oakshire, next to Captain Seraphina (ITSY BITSPY SPIDER) quest giver
        Collider toOakshire = new Collider(Instances.ELADRADOR, new Pos(2088, 89, 2932),
                                                                new Pos(2088, 95, 2941));
        toOakshire.onCollisionEnter(Triggers.playerCharacter(this::toOakshire));
        physicsManager.addCollider(toOakshire);
        Debug.showCollider(toOakshire);

        // from Captain Seraphina to dungeon entrance
        Collider toDungeonEntrance = new Collider(Instances.ELADRADOR, new Pos(2778, 84, 2956),
                                                                        new Pos(2782, 88, 2956));
        toDungeonEntrance.onCollisionEnter(Triggers.playerCharacter(this::toDungeonEntrance));
        physicsManager.addCollider(toDungeonEntrance);
        Debug.showCollider(toDungeonEntrance);

        // from beginning of dungeon to Orion
        Collider toOrion = new Collider(Instances.BROODMOTHER_LAIR, new Pos(132, 117, 189),
                                                                    new Pos(136, 122, 189));
        toOrion.onCollisionEnter(Triggers.playerCharacter(this::toOrion));
        physicsManager.addCollider(toOrion);
        Debug.showCollider(toOrion);

        // from Orion to Broodmother boss
        Collider toBoss = new Collider(Instances.BROODMOTHER_LAIR, new Pos(55, 66, 186),
                                                                    new Pos(60, 70, 186));
        toBoss.onCollisionEnter(Triggers.playerCharacter(this::toBoss));
        physicsManager.addCollider(toBoss);
        Debug.showCollider(toBoss);

        // from back of dungeon to front
        Collider toFront = new Collider(Instances.ELADRADOR, new Pos(2853, 72, 3635),
                                                                new Pos(2859, 76, 3635));
        toFront.onCollisionEnter(Triggers.playerCharacter(this::toDungeonEntrance));
        physicsManager.addCollider(toFront);
        Debug.showCollider(toFront);

        // from front of dungeon back to Captain Seraphina
        Collider toTurnIn = new Collider(Instances.ELADRADOR, new Pos(2870, 73, 3602),
                                                                new Pos(2873, 76, 3602));
        toTurnIn.onCollisionEnter(Triggers.playerCharacter(this::toOakshire));
        physicsManager.addCollider(toTurnIn);
        Debug.showCollider(toTurnIn);
    }

    private void toOakshire(PlayerCharacter pc) {
        Pos to = new Pos(2784, 84, 2959, 180, 0);
        pc.setPosition(to);
    }

    private void toDungeonEntrance(PlayerCharacter pc) {
        Pos to = new Pos(2868, 73, 3599);
        pc.setPosition(to);
    }

    private void toOrion(PlayerCharacter pc) {
        Pos to = new Pos(55, 68, 195, -90, 0);
        pc.setPosition(to);
    }

    private void toBoss(PlayerCharacter pc) {
        Pos to = new Pos(76, 17, 130, 180, 0);
        pc.setPosition(to);
    }
}