package net.mcquest.core.item;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.ItemConsumeEvent;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.PlayerInventoryItemChangeEvent;
import net.minestom.server.event.item.PickupItemEvent;
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
    private final Map<String, Item> itemsById;

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
        eventHandler.addListener(PlayerInventoryItemChangeEvent.class, this::handleChangeItemSlot);
        eventHandler.addListener(PickupItemEvent.class, this::handlePickupItem);
    }

    private void registerItem(Item item) {
        String id = item.getId();
        if (itemsById.containsKey(id)) {
            throw new IllegalArgumentException("ID already in use: " + id);
        }
        itemsById.put(id, item);
    }

    /**
     * Returns the Item with the given ID, or null if none exists.
     */
    public @Nullable Item getItem(String id) {
        return itemsById.get(id);
    }

    /**
     * Returns the Item with the given ItemStack, or null if none exists.
     */
    public @Nullable Item getItem(@NotNull ItemStack itemStack) {
        if (!itemStack.hasTag(Item.ID_TAG)) {
            return null;
        }
        String id = itemStack.getTag(Item.ID_TAG);
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

        Player player = event.getPlayer();
        PlayerCharacter pc = mmorpg.getPlayerCharacterManager().getPlayerCharacter(player);

        if (pc == null) {
            return;
        }

        int slot = event.getSlot();
        if (!(slot == PlayerCharacterInventory.HOTBAR_CONSUMABLE_SLOT_1 ||
                slot == PlayerCharacterInventory.HOTBAR_CONSUMABLE_SLOT_2)) {
            return;
        }

        event.setCancelled(true);

        if (!pc.canAct()) {
            return;
        }

        ItemStack itemStack = player.getInventory().getItemStack(slot);
        ConsumableItem item = (ConsumableItem) getItem(itemStack);
        if (item == null) {
            return;
        }

        pc.sendMessage(ItemUtility.useItemText(item));

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

    private void handleChangeItemSlot(PlayerInventoryItemChangeEvent event) {
        int slot = event.getSlot();
        if (slot == 8) {
            handleChangeWeaponSlot(event);
        } else if (41 <= slot && slot <= 44) {
            handleChangeArmorSlot(event);
        }
    }

    private void handleChangeWeaponSlot(PlayerInventoryItemChangeEvent event) {
        // TODO
    }

    private void handleChangeArmorSlot(PlayerInventoryItemChangeEvent event) {
        Player player = event.getPlayer();
        PlayerCharacter pc = mmorpg.getPlayerCharacterManager().getPlayerCharacter(player);

        if (!event.getPreviousItem().isAir()) {
            // The player unequipped an item
            ArmorItem prevItem = (ArmorItem) getItem(event.getPreviousItem());
            prevItem.unequip(pc);
        }
        if (!event.getNewItem().isAir()) {
            // The player equipped an item
            ArmorItem newItem = (ArmorItem) getItem(event.getNewItem());
            newItem.equip(pc);
        }
    }

    private void handlePickupItem(PickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            event.setCancelled(true);
            return;
        }

        ItemStack itemStack = event.getItemStack();
        Item item = getItem(itemStack);

        if (item == null) {
            event.setCancelled(true);
            return;
        }

        PlayerCharacter pc = mmorpg.getPlayerCharacterManager().getPlayerCharacter(player);
        pc.getInventory().add(item);
    }
}
