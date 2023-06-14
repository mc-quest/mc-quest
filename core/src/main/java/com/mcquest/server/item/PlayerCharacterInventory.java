package com.mcquest.server.item;

import com.mcquest.server.character.PlayerCharacter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

// TODO: handle item events for all methods
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
            if (itemManager.getItem(itemStack) == item) {
                count += itemStack.amount();
            }
        }

        return count;
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
        }

        return added;
    }

    /**
     * Returns whether there is enough space to add the items.
     */
    public boolean canAdd(Map<@NotNull Item, @NotNull Integer> items) {
        PlayerInventory inventory = inventory();

        ItemStack[] contents = new ItemStack[36];
        for (int slot = 0; slot < 36; slot++) {
            contents[slot] = inventory.getItemStack(slot);
        }

        boolean canAdd = true;

        for (Map.Entry<Item, Integer> e : items.entrySet()) {
            Item item = e.getKey();
            int amount = e.getValue();

            // Check occupied slots first.
            for (int slot = 0; slot < 36 && amount > 0; slot++) {
                ItemStack slotItemStack = contents[slot];
                if (slotItemStack.isAir()) {
                    continue;
                }

                Item slotItem = itemManager.getItem(slotItemStack);
                if (slotItem != item) {
                    continue;
                }

                int add = Math.min(amount, item.getStackSize() - slotItemStack.amount());
                contents[slot] = slotItemStack.withAmount(slotItemStack.amount() + add);
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
                canAdd = false;
                break;
            }
        }

        return canAdd;
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

        for (int slot = 0; slot < 36; slot++) {
            if (slot == Weapon.HOTBAR_SLOT) {
                continue;
            }

            ItemStack slotItemStack = inventory.getItemStack(slot);
            if (slotItemStack.isAir()) {
                continue;
            }

            if (itemManager.getItem(slotItemStack) == item) {
                int remove = Math.min(slotItemStack.amount(), amount);
                removed += remove;
                inventory.setItemStack(slot, slotItemStack.withAmount(
                        slotItemStack.amount() - remove));

                if (removed == amount) {
                    break;
                }
            }
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

            if (itemManager.getItem(itemStack) == item) {
                removed += itemStack.amount();
                inventory.setItemStack(slot, ItemStack.AIR);
            }
        }

        if (removed > 0) {
            pc.sendMessage(removedItemsMessage(item, removed));
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
