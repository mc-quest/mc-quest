package com.mcquest.server.features;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.feature.Feature;
import com.mcquest.core.loot.ItemPoolEntry;
import com.mcquest.core.loot.LootChest;
import com.mcquest.core.loot.LootTable;
import com.mcquest.core.loot.Pool;
import com.mcquest.core.object.ObjectManager;
import com.mcquest.core.object.ObjectSpawner;
import com.mcquest.core.physics.Collider;
import com.mcquest.core.physics.PhysicsManager;
import com.mcquest.core.physics.Triggers;
import com.mcquest.core.util.Debug;
import com.mcquest.server.constants.*;
import com.mcquest.server.npc.Broodmother;
import com.mcquest.server.npc.GuardThomas;
import com.mcquest.server.npc.Spider;
import net.minestom.server.coordinate.Pos;

import java.time.Duration;

public class BroodmotherLair implements Feature {
    private Mmorpg mmorpg;

    @Override
    public void hook(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        portals();
        npcs();
        lootChests();
    }

    private void portals() {
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();

        Collider enter = new Collider(Instances.ELADRADOR, new Pos(1, 69, 1), new Pos(6, 74, 6));
        enter.onCollisionEnter(Triggers.playerCharacter(this::enter));
        physicsManager.addCollider(enter);
        Debug.showCollider(enter);

        Collider exit = new Collider(Instances.BROODMOTHER_LAIR, new Pos(0, 0, 0), new Pos(0, 0, 0));
        exit.onCollisionEnter(Triggers.playerCharacter(this::exitMain));
        physicsManager.addCollider(exit);

        Collider bossExit = new Collider(Instances.BROODMOTHER_LAIR, new Pos(0, 0, 0), new Pos(0, 0, 0));
        bossExit.onCollisionEnter(Triggers.playerCharacter(this::exitBoss));
        physicsManager.addCollider(bossExit);
    }

    private void enter(PlayerCharacter pc) {
        Pos to = new Pos(76, 17, 130, 180, 0); // new Pos(138, 116, 181, 180f, 0.0f);
        pc.setInstance(Instances.BROODMOTHER_LAIR, to);
        pc.setZone(Zones.BROODMOTHER_LAIR);
        pc.getMusicPlayer().setSong(Music.BROODMOTHER_LAIR);
        pc.setRespawnPoint(Instances.BROODMOTHER_LAIR, to);
        pc.getMapViewer().setMap(Maps.MELCHER); // TODO: update map
    }

    private void exitMain(PlayerCharacter pc) {
        exit(pc, new Pos(0, 69, 0));
    }

    private void exitBoss(PlayerCharacter pc) {
        exit(pc, new Pos(0, 69, 0));
    }

    private void exit(PlayerCharacter pc, Pos to) {
        pc.setInstance(Instances.ELADRADOR, to);
        pc.setZone(Zones.OAKSHIRE); // TODO: update zone
        pc.getMusicPlayer().setSong(Music.WILDERNESS);
        pc.setRespawnPoint(Instances.ELADRADOR, to);
        pc.getMapViewer().setMap(Maps.MELCHER); // TODO: update map
    }

    private void npcs() {
        guardThomas();
        adventurers();
        spiders();
        broodlingEggs();
        broodmother();
    }

    private void guardThomas() {
        Pos position = new Pos(59.5, 68.0, 194.5, -145.0f, 0.0f);
        ObjectSpawner spawner = ObjectSpawner.of(Instances.BROODMOTHER_LAIR, position, GuardThomas::new);
        mmorpg.getObjectManager().add(spawner);
    }

    private void adventurers() {
        Pos[] positions = {
                new Pos(57.6, 67.0, 190.0, 175.2f, 17.5f),
                new Pos(64.4, 69.0, 189.3, 56.0f, 8.5f),
                new Pos(65.3, 70.0, 194.9, -101.2f, 2.2f)
        };

        ObjectManager objectManager = mmorpg.getObjectManager();
        for (Pos position : positions) {
            // TODO
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
        };

        ObjectManager objectManager = mmorpg.getObjectManager();
        for (Pos position : positions) {
            // BroodlingEgg broodlingEgg = new BroodlingEgg(Instances.BROODMOTHER_LAIR, position);
            // objectManager.add(broodlingEgg);
        }
    }

    private void broodmother() {
        mmorpg.getObjectManager().add(ObjectSpawner.of(
                Instances.BROODMOTHER_LAIR,
                new Pos(73, 6, 92),
                Broodmother::new
        ));
    }

    private void lootChests() {
        ObjectManager objectManager = mmorpg.getObjectManager();

        objectManager.add(ObjectSpawner.of(
                Instances.BROODMOTHER_LAIR,
                new Pos(138, 116, 181, 180f, 0.0f), // new Pos(63.5, 30.0, 214.5, 125f, 0f),
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
}