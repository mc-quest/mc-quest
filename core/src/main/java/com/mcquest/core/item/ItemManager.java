package com.mcquest.core.item;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.event.ItemConsumeEvent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ItemManager {
    private final Mmorpg mmorpg;
    private final Map<Integer, Item> itemsById;

    @ApiStatus.Internal
    public ItemManager(Mmorpg mmorpg, Item[] items) {
        this.mmorpg = mmorpg;

        itemsById = new HashMap<>();
        for (Item item : items) {
            registerItem(item);
        }

        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerLoginEvent.class, this::handleLogin);
        eventHandler.addListener(PlayerChangeHeldSlotEvent.class, this::handleChangeHeldSlot);
    }

    private void registerItem(Item item) {
        int id = item.getId();
        if (itemsById.containsKey(id)) {
            throw new IllegalArgumentException("ID already in use: " + id);
        }
        itemsById.put(id, item);
    }

    /**
     * Returns the Item with the given ID, or null if none exists.
     */
    public @Nullable Item getItem(int id) {
        return itemsById.get(id);
    }

    /**
     * Returns the Item with the given ItemStack, or null if none exists.
     */
    public @Nullable Item getItem(@NotNull ItemStack itemStack) {
        if (!itemStack.hasTag(Item.ID_TAG)) {
            return null;
        }
        int id = itemStack.getTag(Item.ID_TAG);
        return itemsById.get(id);
    }

    public Collection<Item> getItems() {
        return Collections.unmodifiableCollection(itemsById.values());
    }

    private void handleLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        scheduler.buildTask(() -> player.setHeldItemSlot((byte) PlayerCharacterInventory.WEAPON_SLOT))
                .delay(TaskSchedule.nextTick())
                .schedule();
    }

    private void handleChangeHeldSlot(PlayerChangeHeldSlotEvent event) {
        // TODO: sound

        int slot = event.getSlot();
        if (!(slot == PlayerCharacterInventory.HOTBAR_CONSUMABLE_SLOT_1 ||
                slot == PlayerCharacterInventory.HOTBAR_CONSUMABLE_SLOT_2)) {
            return;
        }

        event.setCancelled(true);

        Player player = event.getPlayer();
        PlayerCharacter pc = mmorpg.getPlayerCharacterManager().getPlayerCharacter(player);
        if (!pc.canAct()) {
            return;
        }

        ItemStack itemStack = player.getInventory().getItemStack(slot);
        ConsumableItem item = (ConsumableItem) getItem(itemStack);
        if (item == null) {
            return;
        }

        ItemConsumeEvent consumeEvent = new ItemConsumeEvent(pc, item);
        item.onConsume().emit(consumeEvent);
        MinecraftServer.getGlobalEventHandler().call(consumeEvent);

        PlayerInventory inventory = player.getInventory();
        if (itemStack.amount() == 1) {
            // TODO: set placeholder
            inventory.setItemStack(slot, ItemStack.AIR);
        } else {
            inventory.setItemStack(slot, itemStack.withAmount(itemStack.amount() - 1));
        }
    }
}
