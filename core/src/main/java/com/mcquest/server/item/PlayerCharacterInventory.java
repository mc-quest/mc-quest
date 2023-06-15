package com.mcquest.server.item;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.event.ItemReceiveEvent;
import com.mcquest.server.event.ItemRemoveEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class PlayerCharacterInventory {
    private final PlayerCharacter pc;
    private final ItemManager itemManager;
    private Weapon savedWeapon;

    @ApiStatus.Internal
    public PlayerCharacterInventory(PlayerCharacter pc, ItemManager itemManager) {
        this.pc = pc;
        this.itemManager = itemManager;
        savedWeapon = null;
    }

    public @NotNull Weapon getWeapon() {
        if (savedWeapon != null) {
            return savedWeapon;
        }

        ItemStack itemStack = inventory().getItemStack(Weapon.HOTBAR_SLOT);
        return (Weapon) itemManager.getItem(itemStack);
    }

    public @Nullable ArmorItem getArmor(@NotNull ArmorSlot slot) {
        int inventorySlot = switch (slot) {
            case FEET -> 36;
            case LEGS -> 37;
            case CHEST -> 38;
            case HEAD -> 39;
        };
        ItemStack itemStack = inventory().getItemStack(inventorySlot);
        return (ArmorItem) itemManager.getItem(itemStack);
    }

    /**
     * Returns how much of the item is in this inventory. Equipped weapons,
     * equipped armor, and items on the cursor are not counted.
     */
    public int count(@NotNull Item item) {
        PlayerInventory inventory = inventory();
        int count = 0;

        for (int slot = 0; slot < 36; slot++) {
            if (slot == Weapon.HOTBAR_SLOT) {
                continue;
            }

            ItemStack itemStack = inventory.getItemStack(slot);
            if (itemStack.isAir()) {
                continue;
            }

            if (itemManager.getItem(itemStack) == item) {
                count += itemStack.amount();
            }
        }

        return count;
    }

    /**
     * Returns whether there is enough space to add the items.
     */
    public boolean canAdd(@NotNull Map<@NotNull Item, @NotNull Integer> items) {
        for (Integer amount : items.values()) {
            if (amount < 0) {
                throw new IllegalArgumentException();
            }
        }

        // It's not necessary to skip weapon here.

        PlayerInventory inventory = inventory();

        ItemStack[] contents = new ItemStack[36];
        for (int slot = 0; slot < 36; slot++) {
            contents[slot] = inventory.getItemStack(slot);
        }

        for (Map.Entry<Item, Integer> e : items.entrySet()) {
            Item item = e.getKey();
            int amount = e.getValue();

            // Check occupied slots first.
            for (int slot = 0; slot < 36 && amount > 0; slot++) {
                ItemStack itemStack = contents[slot];
                if (itemStack.isAir()) {
                    continue;
                }

                if (itemManager.getItem(itemStack) != item) {
                    continue;
                }

                int add = Math.min(amount, item.getStackSize() - itemStack.amount());
                contents[slot] = itemStack.withAmount(itemStack.amount() + add);
                amount -= add;
            }

            // Now check empty slots.
            for (int slot = 0; slot < 36 && amount > 0; slot++) {
                if (!contents[slot].isAir()) {
                    continue;
                }

                int add = Math.min(amount, item.getStackSize());
                contents[slot] = item.getItemStack().withAmount(add);
                amount -= add;
            }

            if (amount > 0) {
                return false;
            }
        }

        return true;
    }

    public boolean add(@NotNull Item item) {
        return add(item, 1) == 1;
    }

    public int add(@NotNull Item item, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException();
        }

        ItemStack itemStack = item.getItemStack();
        PlayerInventory inventory = inventory();

        int added;
        for (added = 0; added < amount; added++) {
            if (!inventory.addItemStack(itemStack)) {
                break;
            }
        }

        if (added > 0) {
            pc.sendMessage(addedItemsMessage(item, added));
            ItemReceiveEvent event = new ItemReceiveEvent(pc, item, added);
            MinecraftServer.getGlobalEventHandler().call(event);
        }

        return added;
    }

    private boolean canRemove(@NotNull Map<@NotNull Item, @NotNull Integer> items) {
        for (Integer amount : items.values()) {
            if (amount < 0) {
                throw new IllegalArgumentException();
            }
        }

        PlayerInventory inventory = inventory();
        Map<Item, Integer> remaining = new HashMap<>(items);

        // First remove entries whose amount is 0.
        remaining.entrySet().removeIf(e -> e.getValue() == 0);

        for (int slot = 0; slot < 36 && !remaining.isEmpty(); slot++) {
            if (slot == Weapon.HOTBAR_SLOT) {
                continue;
            }

            ItemStack itemStack = inventory.getItemStack(slot);
            if (itemStack.isAir()) {
                continue;
            }

            Item item = itemManager.getItem(itemStack);
            if (item == null || !remaining.containsKey(item)) {
                continue;
            }

            int amount = remaining.get(item);
            if (itemStack.amount() < amount) {
                remaining.put(item, amount - itemStack.amount());
            } else {
                remaining.remove(item);
            }
        }

        return remaining.isEmpty();
    }

    public boolean remove(Item item) {
        return remove(item, 1) == 1;
    }

    public int remove(Item item, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException();
        }

        PlayerInventory inventory = inventory();
        int removed = 0;

        for (int slot = 0; slot < 36 && removed < amount; slot++) {
            if (slot == Weapon.HOTBAR_SLOT) {
                continue;
            }

            ItemStack itemStack = inventory.getItemStack(slot);
            if (itemStack.isAir()) {
                continue;
            }

            if (itemManager.getItem(itemStack) != item) {
                continue;
            }

            int remove = Math.min(itemStack.amount(), amount - removed);
            inventory.setItemStack(slot, itemStack.withAmount(
                    itemStack.amount() - remove));
            removed += remove;
        }

        if (removed > 0) {
            pc.sendMessage(removedItemsMessage(item, removed));
            ItemRemoveEvent event = new ItemRemoveEvent(pc, item, removed);
            MinecraftServer.getGlobalEventHandler().call(event);
        }

        return removed;
    }

    public int removeAll(Item item) {
        PlayerInventory inventory = inventory();
        int removed = 0;

        for (int slot = 0; slot < 36; slot++) {
            if (slot == Weapon.HOTBAR_SLOT) {
                continue;
            }

            ItemStack itemStack = inventory.getItemStack(slot);
            if (itemStack.isAir()) {
                continue;
            }

            if (itemManager.getItem(itemStack) != item) {
                continue;
            }

            removed += itemStack.amount();
            inventory.setItemStack(slot, ItemStack.AIR);
        }

        if (removed > 0) {
            pc.sendMessage(removedItemsMessage(item, removed));
            ItemRemoveEvent event = new ItemRemoveEvent(pc, item, removed);
            MinecraftServer.getGlobalEventHandler().call(event);
        }

        return removed;
    }

    @ApiStatus.Internal
    public void saveWeapon() {
        savedWeapon = getWeapon();
    }

    @ApiStatus.Internal
    public void unsaveWeapon() {
        inventory().setItemStack(Weapon.HOTBAR_SLOT, savedWeapon.getItemStack());
        savedWeapon = null;
    }

    private PlayerInventory inventory() {
        return pc.getPlayer().getInventory();
    }

    private Component addedItemsMessage(Item item, int added) {
        if (added == 1) {
            return Component.text("Received ", NamedTextColor.GRAY)
                    .append(item.getDisplayName());
        }

        return Component.text("Received " + added + " ", NamedTextColor.GRAY)
                .append(item.getDisplayName());
    }

    private Component removedItemsMessage(Item item, int removed) {
        if (removed == 1) {
            return Component.text("Removed ", NamedTextColor.GRAY)
                    .append(item.getDisplayName());
        }

        return Component.text("Removed " + removed + " ", NamedTextColor.GRAY)
                .append(item.getDisplayName());
    }
}
