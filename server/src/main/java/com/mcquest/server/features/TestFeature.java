package com.mcquest.server.features;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.commerce.Money;
import com.mcquest.server.constants.*;
import com.mcquest.server.event.PlayerCharacterLoginEvent;
import com.mcquest.server.event.PlayerCharacterMoveEvent;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.item.Item;
import com.mcquest.server.item.PlayerCharacterInventory;
import com.mcquest.server.loot.*;
import com.mcquest.server.physics.Collider;
import com.mcquest.server.util.Debug;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.timer.SchedulerManager;

import java.io.File;
import java.time.Duration;
import java.util.Map;

public class TestFeature implements Feature {
    @Override
    public void hook(Mmorpg mmorpg) {
        mmorpg.getGlobalEventHandler().addListener(PlayerCharacterMoveEvent.class, event -> {
            PlayerCharacter pc = event.getPlayerCharacter();
            PlayerCharacterInventory inventory = pc.getInventory();
            Map<Item, Integer> items = Map.of(Items.TEST_ITEM, 2239);
            Map<Item, Integer> items2 = Map.of(Items.TEST_ITEM, 2240);
        });
        Collider eladradorTrigger = new Collider(Instances.ELADRADOR, new Pos(5, 72, 5), new Vec(5));
        Collider bulskanTrigger = new Collider(Instances.BULSKAN_RUINS, new Pos(0, -56, 0), new Vec(5));
        eladradorTrigger.onCollisionEnter(other -> {
            if (other instanceof PlayerCharacter.Hitbox hitbox) {
                PlayerCharacter pc = hitbox.getCharacter();
                pc.setPosition(new Pos(5, -58, 5));
                pc.setInstance(Instances.BULSKAN_RUINS);
                pc.setZone(Zones.BULSKAN_RUINS);
                pc.getMusicPlayer().setSong(Music.DUNGEON);
            }
        });
        bulskanTrigger.onCollisionEnter(other -> {
            if (other instanceof PlayerCharacter.Hitbox hitbox) {
                PlayerCharacter pc = hitbox.getCharacter();
                pc.setPosition(new Pos(0, 69, 0));
                pc.setInstance(Instances.ELADRADOR);
                pc.setZone(Zones.OAKSHIRE);
                pc.getMusicPlayer().setSong(Music.WILDERNESS);
            }
        });
        mmorpg.getPhysicsManager().addCollider(eladradorTrigger);
        mmorpg.getPhysicsManager().addCollider(bulskanTrigger);
        Debug.showCollider(eladradorTrigger);
        Debug.showCollider(bulskanTrigger);
        mmorpg.getGlobalEventHandler().addListener(PlayerChatEvent.class, event -> {
            Player player = event.getPlayer();
            PlayerCharacter pc = mmorpg.getPlayerCharacterManager().getPlayerCharacter(player);
            pc.getInventory().add(Items.ADVENTURERS_SWORD);
            //            Instance newInstance;
//            Pos newPosition;
//            if (pc.getInstance() == Instances.ELADRADOR) {
//                newInstance = Instances.BULSKAN_RUINS;
//                newPosition = new Pos(0, -59, 0);
//            } else {
//                newInstance = Instances.ELADRADOR;
//                newPosition = new Pos(0, 69, 0);
//            }
//            pc.setInstance(newInstance);
//            pc.setPosition(newPosition);
        });


        mmorpg.getResourcePackManager().writeResourcePack(new File("resourcepack.zip"));
        LootChestManager lootChestManager = mmorpg.getLootChestManager();
        SchedulerManager scheduler = mmorpg.getSchedulerManager();
        LootTable lootTable = LootTable.builder()
                .pool(Pool.builder()
                        .rolls(100)
                        .entry(ItemPoolEntry.builder(Items.TEST_ITEM).amount(4).build())
                        .entry(MoneyPoolEntry.builder().value(Money.copper(3), Money.copper(20)).weight(2).build())
                        .build())
                .build();
        LootChest lootChest = new LootChest(Instances.ELADRADOR, Positions.SPAWN_POSITION, lootTable);
        lootChest.onOpen().subscribe(event ->
                scheduler.buildTask(() ->
                        lootChestManager.addLootChest(lootChest)).delay(Duration.ofSeconds(1)).schedule()
        );
        lootChestManager.addLootChest(lootChest);
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        eventHandler.addListener(PlayerCharacterLoginEvent.class, this::handleLevelUp);
        mmorpg.getGlobalEventHandler().addListener(PlayerCharacterLoginEvent.class, event -> {
            PlayerCharacter pc = event.getPlayerCharacter();
            pc.getInventory().add(Items.TEST_ITEM);
            pc.getMapManager().setMap(Maps.MELCHER);
            Player player = pc.getPlayer();
            // player.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(1);
            player.getAttribute(Attribute.FLYING_SPEED).setBaseValue(1);
            player.setAllowFlying(true);
        });
//        BufferedImage map = MapMaker.createMap(Instances.ELADRADOR, -1000, -500, 1000, 1000, 80);
//        try {
//            ImageIO.write(map, "png", new File("map.png"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void handleLevelUp(PlayerCharacterLoginEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        pc.setMaxMana(100);
        pc.setMana(100);
    }
}
