package com.mcquest.core.item;

import com.google.common.base.Predicates;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.persistence.PersistentItem;
import com.mcquest.core.persistence.PlayerCharacterData;
import com.mcquest.core.quest.QuestObjective;
import com.mcquest.core.resourcepack.CustomModelData;
import com.mcquest.core.resourcepack.Materials;
import com.mcquest.core.util.ItemStackUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerCharacterInventory {
    // TODO: should probably be private
    public static final int WEAPON_SLOT = 8;
    static final int HOTBAR_CONSUMABLE_SLOT_1 = 6;
    static final int HOTBAR_CONSUMABLE_SLOT_2 = 7;
    static final int MIN_SLOT = 9;
    static final int MAX_SLOT = 35;

    private final PlayerCharacter pc;
    private final ItemManager itemManager;
    private Weapon savedWeapon;

    @ApiStatus.Internal
    public PlayerCharacterInventory(PlayerCharacter pc, PlayerCharacterData data,
                                    ItemManager itemManager) {
        this.pc = pc;
        this.itemManager = itemManager;
        savedWeapon = null;
        loadItems(data.getItems());

        // TODO: should be responsible for serializing inventory
        // TODO: inventory conditions
    }

    public @NotNull Weapon getWeapon() {
        if (savedWeapon != null) {
            return savedWeapon;
        }

        return (Weapon) getItem(WEAPON_SLOT);
    }

    public @Nullable ArmorItem getArmor(@NotNull ArmorSlot slot) {
        int inventorySlot = switch (slot) {
            case FEET -> 44;
            case LEGS -> 43;
            case CHEST -> 42;
            case HEAD -> 41;
        };

        return (ArmorItem) getItem(inventorySlot);
    }

    public @Nullable ConsumableItem getHotbarConsumable1() {
        return (ConsumableItem) getItem(HOTBAR_CONSUMABLE_SLOT_1);
    }

    public @Nullable ConsumableItem getHotbarConsumable2() {
        return (ConsumableItem) getItem(HOTBAR_CONSUMABLE_SLOT_2);
    }

    public boolean contains(@NotNull KeyItem item) {
        PlayerInventory inventory = inventory();

        for (int slot = MIN_SLOT; slot <= MAX_SLOT; slot++) {
            ItemStack itemStack = inventory.getItemStack(slot);
            if (itemStack.isAir()) {
                continue;
            }

            if (itemManager.getItem(itemStack) == item) {
                return true;
            }
        }

        return false;
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

        PlayerInventory inventory = inventory();
        Map<Item, Integer> remaining = new HashMap<>(items);

        // Quest items do not occupy inventory space.
        remaining.keySet().removeIf(Predicates.instanceOf(QuestItem.class));

        // Remove entries whose amount is 0.
        remaining.entrySet().removeIf(e -> e.getValue() == 0);

        // Check occupied slots first.
        for (int slot = MIN_SLOT; slot <= MAX_SLOT && !remaining.isEmpty(); slot++) {
            ItemStack itemStack = inventory.getItemStack(slot);
            if (itemStack.isAir()) {
                continue;
            }

            Item item = itemManager.getItem(itemStack);
            if (!remaining.containsKey(item)) {
                continue;
            }

            int amount = remaining.get(item);
            int capacity = item.getStackSize() - itemStack.amount();
            if (capacity < amount) {
                remaining.put(item, amount - capacity);
            } else {
                remaining.remove(item);
            }
        }

        // Now check empty slots.
        for (int slot = MIN_SLOT; slot <= MAX_SLOT && !remaining.isEmpty(); slot++) {
            if (!inventory.getItemStack(slot).isAir()) {
                continue;
            }

            Map.Entry<Item, Integer> e = remaining.entrySet().iterator().next();
            Item item = e.getKey();
            int amount = e.getValue();

            if (item.getStackSize() < amount) {
                remaining.put(item, amount - item.getStackSize());
            } else {
                remaining.remove(item);
            }
        }

        return remaining.isEmpty();
    }

    public boolean add(@NotNull Item item) {
        return add(item, 1) == 1;
    }

    public int add(@NotNull Item item, int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException();
        }

        if (item instanceof QuestItem questItem) {
            QuestObjective objective = questItem.getObjective();
            if (objective != null) {
                objective.addProgress(pc, amount);
            }
            return amount;
        }

        PlayerInventory inventory = inventory();
        int added = 0;

        // Check occupied slots first.
        for (int slot = MIN_SLOT; slot <= MAX_SLOT && added < amount; slot++) {
            ItemStack itemStack = inventory.getItemStack(slot);
            if (itemStack.isAir()) {
                continue;
            }

            if (itemManager.getItem(itemStack) != item) {
                continue;
            }

            int capacity = item.getStackSize() - itemStack.amount();
            if (capacity == 0) {
                continue;
            }

            int add = Math.min(capacity, amount - added);
            inventory.setItemStack(slot, itemStack.withAmount(itemStack.amount() + add));
            added += add;
        }

        // Now check empty slots
        for (int slot = MIN_SLOT; slot <= MAX_SLOT && added < amount; slot++) {
            if (!inventory.getItemStack(slot).isAir()) {
                continue;
            }

            int add = Math.min(item.getStackSize(), amount - added);
            inventory.setItemStack(slot, item.getItemStack().withAmount(add));
            added += add;
        }

        return added;
    }

    @ApiStatus.Internal
    public void saveWeapon() {
        savedWeapon = getWeapon();
    }

    @ApiStatus.Internal
    public void unsaveWeapon() {
        inventory().setItemStack(WEAPON_SLOT, savedWeapon.getItemStack());
        savedWeapon = null;
    }

    private PlayerInventory inventory() {
        return pc.getEntity().getInventory();
    }

    private void loadItems(PersistentItem[] items) {
        PlayerInventory inventory = inventory();

        for (PersistentItem persistentItem : items) {
            if (persistentItem == null) {
                continue;
            }


            Item item = itemManager.getItem(persistentItem.getItemId());
            ItemStack itemStack = item.getItemStack()
                    .withAmount(persistentItem.getAmount());
            inventory.setItemStack(persistentItem.getSlot(), itemStack);
        }

        // TODO other slots (weapon, armor, consumables)
        inventory.setItemStack(6, hotbarConsumablePlaceholder(0, false));
        inventory.setItemStack(7, hotbarConsumablePlaceholder(1, false));
    }

    private ItemStack hotbarConsumablePlaceholder(int slot, boolean flashing) {
        String nameContent = String.format("Consumable Slot %d", slot + 1);
        TextComponent displayName = Component.text(nameContent, NamedTextColor.YELLOW);
        List<TextComponent> lore = List.of(Component.text("Consumable items go here"));
        return ItemStackUtility.create(Materials.GUI, displayName, lore)
                .meta(builder -> builder.customModelData(CustomModelData.HOTBAR_CONSUMABLE_PLACEHOLDER))
                .build();
    }

    private Item getItem(int slot) {
        ItemStack itemStack = inventory().getItemStack(slot);
        if (itemStack.isAir()) {
            return null;
        }

        return itemManager.getItem(itemStack);
    }
}
