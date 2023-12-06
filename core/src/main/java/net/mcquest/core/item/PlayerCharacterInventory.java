package net.mcquest.core.item;

import com.google.common.base.Predicates;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.ItemConsumeEvent;
import net.mcquest.core.event.WeaponEquipEvent;
import net.mcquest.core.event.WeaponUnequipEvent;
import net.mcquest.core.persistence.PersistentInventory;
import net.mcquest.core.persistence.PersistentItem;
import net.mcquest.core.persistence.PlayerCharacterData;
import net.mcquest.core.quest.QuestObjective;
import net.mcquest.core.resourcepack.CustomModelData;
import net.mcquest.core.resourcepack.Materials;
import net.mcquest.core.util.ItemStackUtility;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.inventory.condition.InventoryConditionResult;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerCharacterInventory {
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
        loadInventory(data.inventory());
        inventory().addInventoryCondition(this::handleInventoryClick);
        pc.getEntity().eventNode().addListener(
                PlayerChangeHeldSlotEvent.class,
                this::handleChangeHeldSlot
        );
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

    private void loadInventory(PersistentInventory data) {
        PlayerInventory inventory = inventory();

        PersistentItem[] storage = data.storage();
        for (int i = 0; i < storage.length; i++) {
            PersistentItem storageItem = storage[i];

            if (storageItem == null) {
                continue;
            }

            Item item = itemManager.getItem(storageItem.itemId());
            inventory.setItemStack(
                    i + 9,
                    item.getItemStack().withAmount(storageItem.amount())
            );
        }

        inventory.setItemStack(
                WEAPON_SLOT,
                itemManager.getItem(data.weaponId()).getItemStack()
        );

        if (data.feetArmorId() != null) {
            inventory.setItemStack(
                    44,
                    itemManager.getItem(data.feetArmorId()).getItemStack()
            );
        }

        if (data.legsArmorId() != null) {
            inventory.setItemStack(
                    43,
                    itemManager.getItem(data.legsArmorId()).getItemStack()
            );
        }

        if (data.chestArmorId() != null) {
            inventory.setItemStack(
                    42,
                    itemManager.getItem(data.chestArmorId()).getItemStack()
            );
        }

        if (data.headArmorId() != null) {
            inventory.setItemStack(
                    41,
                    itemManager.getItem(data.headArmorId()).getItemStack()
            );
        }

        if (data.hotbarConsumable1() != null) {
            inventory.setItemStack(
                    HOTBAR_CONSUMABLE_SLOT_1,
                    itemManager.getItem(data.hotbarConsumable1().itemId()).getItemStack()
                            .withAmount(data.hotbarConsumable1().amount())
            );
        } else {
            inventory.setItemStack(
                    HOTBAR_CONSUMABLE_SLOT_1,
                    hotbarConsumablePlaceholder(0, false)
            );
        }

        if (data.hotbarConsumable2() != null) {
            inventory.setItemStack(
                    HOTBAR_CONSUMABLE_SLOT_2,
                    itemManager.getItem(data.hotbarConsumable2().itemId()).getItemStack()
                            .withAmount(data.hotbarConsumable2().amount())
            );
        } else {
            inventory.setItemStack(
                    HOTBAR_CONSUMABLE_SLOT_2,
                    hotbarConsumablePlaceholder(1, false)
            );
        }
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

    private ItemStack cursorItemStack() {
        return inventory().getCursorItem();
    }

    private Item cursorItem() {
        ItemStack itemStack = cursorItemStack();
        if (itemStack.isAir()) {
            return null;
        }

        return itemManager.getItem(itemStack);
    }

    private void handleInventoryClick(
            Player player,
            int slot,
            ClickType clickType,
            InventoryConditionResult result
    ) {
        if (slot == WEAPON_SLOT) {
            // TODO: check that player has proficiency in new weapon
            if (!(cursorItem() instanceof Weapon newWeapon)) {
                result.setCancel(true);
                return;
            }
            Weapon oldWeapon = getWeapon();
            oldWeapon.onUnequip().emit(new WeaponUnequipEvent(pc, oldWeapon));
            newWeapon.onEquip().emit(new WeaponEquipEvent(pc, newWeapon));
            return;
        }

        if (slot == HOTBAR_CONSUMABLE_SLOT_1 || slot == HOTBAR_CONSUMABLE_SLOT_2) {
            if (cursorItemStack().isAir() && getItem(slot) != null) {
                result.setCursorItem(hotbarConsumablePlaceholder(slot - 6, false));
            } else {
                if (!(cursorItem() instanceof ConsumableItem)) {
                    result.setCancel(true);
                    return;
                }
                result.setClickedItem(ItemStack.AIR);
            }
        }
    }

    private void handleChangeHeldSlot(PlayerChangeHeldSlotEvent event) {
        // TODO: sound

        int slot = event.getSlot();
        if (!(slot == PlayerCharacterInventory.HOTBAR_CONSUMABLE_SLOT_1 ||
                slot == PlayerCharacterInventory.HOTBAR_CONSUMABLE_SLOT_2)) {
            return;
        }

        event.setCancelled(true);

        if (!pc.canAct()) {
            return;
        }

        ConsumableItem item = (ConsumableItem) getItem(slot);
        if (item == null) {
            return;
        }

        pc.sendMessage(ItemUtility.useItemText(item));

        ItemConsumeEvent consumeEvent = new ItemConsumeEvent(pc, item);
        item.onConsume().emit(consumeEvent);
        MinecraftServer.getGlobalEventHandler().call(consumeEvent);

        ItemStack itemStack = inventory().getItemStack(slot);
        if (itemStack.amount() == 1) {
            inventory().setItemStack(slot, hotbarConsumablePlaceholder(slot - 6, false));
        } else {
            inventory().setItemStack(slot, itemStack.withAmount(itemStack.amount() - 1));
        }
    }

    @ApiStatus.Internal
    public PersistentInventory save() {
        PlayerInventory inventory = inventory();

        ConsumableItem hotbarConsumable1 = getHotbarConsumable1();
        PersistentItem persistentHotbarConsumable1 = hotbarConsumable1 == null ? null :
                new PersistentItem(
                        hotbarConsumable1.getId(),
                        inventory.getItemStack(HOTBAR_CONSUMABLE_SLOT_1).amount()
                );

        ConsumableItem hotbarConsumable2 = getHotbarConsumable1();
        PersistentItem persistentHotbarConsumable2 = hotbarConsumable2 == null ? null :
                new PersistentItem(
                        hotbarConsumable2.getId(),
                        inventory.getItemStack(HOTBAR_CONSUMABLE_SLOT_2).amount()
                );

        PersistentItem[] storage = new PersistentItem[3 * 9];
        for (int i = 0; i < storage.length; i++) {
            ItemStack itemStack = inventory.getItemStack(i + 9);
            if (itemStack.isAir()) {
                continue;
            }

            storage[i] = new PersistentItem(
                    itemManager.getItem(itemStack).getId(),
                    itemStack.amount()
            );
        }

        return new PersistentInventory(
                idOf(getWeapon()),
                idOf(getArmor(ArmorSlot.FEET)),
                idOf(getArmor(ArmorSlot.LEGS)),
                idOf(getArmor(ArmorSlot.CHEST)),
                idOf(getArmor(ArmorSlot.HEAD)),
                persistentHotbarConsumable1,
                persistentHotbarConsumable2,
                storage
        );
    }

    private static String idOf(Item item) {
        return item == null ? null : item.getId();
    }
}
