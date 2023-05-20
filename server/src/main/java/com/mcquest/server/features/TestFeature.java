package com.mcquest.server.features;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.constants.Instances;
import com.mcquest.server.constants.Items;
import com.mcquest.server.constants.Maps;
import com.mcquest.server.constants.Positions;
import com.mcquest.server.event.PlayerCharacterLoginEvent;
import com.mcquest.server.feature.Feature;
import com.mcquest.server.loot.*;
import net.minestom.server.attribute.Attribute;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.timer.SchedulerManager;

import java.time.Duration;

public class TestFeature implements Feature {
    @Override
    public void hook(Mmorpg mmorpg) {
        LootChestManager lootChestManager = mmorpg.getLootChestManager();
        SchedulerManager scheduler = mmorpg.getSchedulerManager();
        LootTable lootTable = LootTable.builder()
                .pool(Pool.builder()
                        .entry(ItemPoolEntry.builder(Items.TEST_ITEM).build())
                        .entry(MoneyPoolEntry.builder().build())
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
            pc.giveItem(Items.TEST_ITEM);
            pc.getMapManager().setMap(Maps.MELCHER);
            Player player = pc.getPlayer();
            player.getAttribute(Attribute.MOVEMENT_SPEED).setBaseValue(1);
            player.getAttribute(Attribute.FLYING_SPEED).setBaseValue(1);
            player.setAllowFlying(true);
        });
    }

    private void handleLevelUp(PlayerCharacterLoginEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        pc.setMaxMana(100);
        pc.setMana(100);
    }

}
