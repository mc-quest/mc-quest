package com.mcquest.server.ui;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.character.PlayerCharacterManager;
import com.mcquest.server.event.PlayerCharacterBasicAttackEvent;
import com.mcquest.server.event.PlayerCharacterConsumeItemEvent;
import com.mcquest.server.event.PlayerCharacterLoginEvent;
import com.mcquest.server.item.ConsumableItem;
import com.mcquest.server.item.Item;
import com.mcquest.server.playerclass.PlayerClassManager;
import com.mcquest.server.playerclass.Skill;
import com.mcquest.server.util.ItemStackUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.event.player.PlayerHandAnimationEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
public class InteractionHandler {
    private static final int WEAPON_SLOT = 4;
    private static final int MENU_SLOT = 8;
    private static final int MENU_SKILL_TREE_SLOT = 2;
    private static final int MENU_QUEST_LOG_SLOT = 4;
    private static final int MENU_LOGOUT_SLOT = 6;
    private static final Component CLICK_TO_OPEN = Component.text("Click to open", NamedTextColor.GRAY);
    private static final ItemStack OPEN_MENU = ItemStackUtility.createItemStack(Material.EMERALD,
            Component.text("Menu", NamedTextColor.GREEN), List.of(CLICK_TO_OPEN));
    private static final ItemStack OPEN_SKILL_TREE = ItemStackUtility
            .createItemStack(Material.IRON_SWORD, Component.text("Skill Tree", NamedTextColor.GREEN),
                    List.of(CLICK_TO_OPEN));
    private static final ItemStack OPEN_QUEST_LOG = ItemStackUtility
            .createItemStack(Material.BOOK, Component.text("Quest Log", NamedTextColor.GREEN),
                    List.of(CLICK_TO_OPEN));
    private static final ItemStack LOGOUT = ItemStackUtility.createItemStack(Material.BARRIER,
            Component.text("Logout", NamedTextColor.GREEN),
            List.of(Component.text("Click to logout", NamedTextColor.GRAY)));

    private final Mmorpg mmorpg;

    public InteractionHandler(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
    }

    public void registerListeners() {
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerCharacterLoginEvent.class, this::handlePlayerCharacterLogin);
        eventHandler.addListener(PickupItemEvent.class, this::handlePickupItem);
        eventHandler.addListener(PlayerChangeHeldSlotEvent.class, this::handleChangeHeldSlot);
        eventHandler.addListener(PlayerHandAnimationEvent.class, this::handleBasicAttack);
    }

    private void handlePlayerCharacterLogin(PlayerCharacterLoginEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        Player player = pc.getPlayer();
        // Must delay a tick, or it won't work.
        mmorpg.getSchedulerManager().buildTask(() -> {
            player.setHeldItemSlot((byte) 4);
        }).delay(TaskSchedule.nextTick()).schedule();
        PlayerInventory inventory = player.getInventory();
        inventory.setItemStack(MENU_SLOT, OPEN_MENU);
        inventory.addInventoryCondition((p, slot, clickType, inventoryConditionResult) -> {
            if (slot == MENU_SLOT) {
                inventoryConditionResult.setCancel(true);
                openMenu(pc);
            } else if (slot == WEAPON_SLOT || p.getOpenInventory() != null) {
                inventoryConditionResult.setCancel(true);
            }
        });
    }

    private void openMenu(PlayerCharacter pc) {
        Inventory menu = new Inventory(InventoryType.CHEST_1_ROW, "Menu");
        menu.setItemStack(MENU_SKILL_TREE_SLOT, OPEN_SKILL_TREE);
        menu.setItemStack(MENU_QUEST_LOG_SLOT, OPEN_QUEST_LOG);
        menu.setItemStack(MENU_LOGOUT_SLOT, LOGOUT);
        menu.addInventoryCondition((player, slot, clickType, inventoryConditionResult) -> {
            switch (slot) {
                case MENU_SKILL_TREE_SLOT:
                    openSkillTree(pc);
                    break;
                case MENU_QUEST_LOG_SLOT:
                    openQuestLog(pc);
                    break;
                case MENU_LOGOUT_SLOT:
                    logout(pc);
                    break;
                default:
                    break;
            }
            inventoryConditionResult.setCancel(true);
        });
        pc.getPlayer().openInventory(menu);
    }

    private void openSkillTree(PlayerCharacter pc) {
        // TODO
    }

    private void openQuestLog(PlayerCharacter pc) {
        // TODO
    }

    private void logout(PlayerCharacter pc) {
        pc.sendMessage(Component.text("Logout not yet implemented", NamedTextColor.RED));
    }

    private void handlePickupItem(PickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
            PlayerCharacter pc = pcManager.getPlayerCharacter(player);
            if (pc == null) {
                return;
            }
            PlayerInventory inventory = player.getInventory();
            boolean successful = inventory.addItemStack(event.getItemStack());
            if (!successful) {
                event.setCancelled(true);
            }
        }
    }

    private void handleChangeHeldSlot(PlayerChangeHeldSlotEvent event) {
        Player player = event.getPlayer();
        PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
        PlayerCharacter pc = pcManager.getPlayerCharacter(player);
        event.setCancelled(true);
        int slot = event.getSlot();
        PlayerInventory inventory = player.getInventory();
        ItemStack itemStack = inventory.getItemStack(slot);
        Item item = mmorpg.getItemManager().getItem(itemStack);
        if (item instanceof ConsumableItem consumableItem) {
            int newAmount = itemStack.amount() - 1;
            inventory.setItemStack(slot, itemStack.withAmount(newAmount));
            pc.sendMessage(Component.text("Used ", NamedTextColor.GRAY).append(item.getDisplayName()));
            GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
            eventHandler.call(new PlayerCharacterConsumeItemEvent(pc, consumableItem));
            return;
        }

        PlayerClassManager playerClassManager = mmorpg.getPlayerClassManager();
        Skill skill = playerClassManager.getSkill(itemStack);
        // TODO: Check for skills/consumables (might want to do skill checking in playerclass package)
    }

    private void handleBasicAttack(PlayerHandAnimationEvent event) {
        Player player = event.getPlayer();
        PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
        PlayerCharacter pc = pcManager.getPlayerCharacter(player);
        if (pc == null) {
            return;
        }
        if (pc.isDisarmed()) {
            return;
        }
        MinecraftServer.getGlobalEventHandler()
                .call(new PlayerCharacterBasicAttackEvent(pc, pc.getWeapon()));
    }
}
