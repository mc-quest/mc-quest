package com.mcquest.server.ui;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.cartography.PlayerCharacterMapManager;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.character.PlayerCharacterManager;
import com.mcquest.server.event.*;
import com.mcquest.server.instance.Instance;
import com.mcquest.server.item.ConsumableItem;
import com.mcquest.server.item.Item;
import com.mcquest.server.item.Weapon;
import com.mcquest.server.physics.Collider;
import com.mcquest.server.physics.PhysicsManager;
import com.mcquest.server.physics.RaycastHit;
import com.mcquest.server.util.ItemStackUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.*;
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
    private static final double MAX_INTERACTION_DISTANCE = 5.0;
    private static final int MENU_SKILL_TREE_SLOT = 1;
    private static final int MENU_QUEST_LOG_SLOT = 3;
    private static final int MENU_MAP_SLOT = 5;
    private static final int MENU_LOGOUT_SLOT = 7;
    private static final Component CLICK_TO_OPEN = Component.text("Click to open", NamedTextColor.GRAY);
    private static final ItemStack OPEN_SKILL_TREE = ItemStackUtility
            .createItemStack(Material.IRON_SWORD, Component.text("Skill Tree", NamedTextColor.YELLOW),
                    List.of(CLICK_TO_OPEN));
    private static final ItemStack OPEN_MAP = ItemStackUtility
            .createItemStack(Material.FILLED_MAP, Component.text("Map", NamedTextColor.YELLOW),
                    List.of(CLICK_TO_OPEN));
    private static final ItemStack OPEN_QUEST_LOG = ItemStackUtility
            .createItemStack(Material.BOOK, Component.text("Quest Log", NamedTextColor.YELLOW),
                    List.of(CLICK_TO_OPEN));
    private static final ItemStack LOGOUT = ItemStackUtility.createItemStack(Material.BARRIER,
            Component.text("Logout", NamedTextColor.YELLOW),
            List.of(Component.text("Click to logout", NamedTextColor.GRAY)));

    private final Mmorpg mmorpg;

    public InteractionHandler(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
    }

    public void registerListeners() {
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerCharacterLoginEvent.class, this::handlePlayerCharacterLogin);
        eventHandler.addListener(PickupItemEvent.class, this::handlePickupItem);
        eventHandler.addListener(PlayerSwapItemEvent.class, this::handleOpenMenu);
        eventHandler.addListener(PlayerChangeHeldSlotEvent.class, this::handleChangeHeldSlot);
        eventHandler.addListener(PlayerHandAnimationEvent.class, this::handleBasicAttack);
        eventHandler.addListener(PlayerEntityInteractEvent.class, this::handleInteract);
        eventHandler.addListener(PlayerUseItemEvent.class, this::handleInteract);
        eventHandler.addListener(PlayerBlockInteractEvent.class, this::handleBlockInteract);
        eventHandler.addListener(ItemDropEvent.class, this::handleItemDrop);
    }

    private void handlePlayerCharacterLogin(PlayerCharacterLoginEvent event) {
        PlayerCharacter pc = event.getPlayerCharacter();
        Player player = pc.getPlayer();
        // Must delay a tick, or it won't work.
        mmorpg.getSchedulerManager()
                .buildTask(() -> player.setHeldItemSlot((byte) Weapon.HOTBAR_SLOT))
                .delay(TaskSchedule.nextTick())
                .schedule();
        PlayerInventory inventory = player.getInventory();
        inventory.addInventoryCondition((p, slot, clickType, inventoryConditionResult) -> {
            if (slot == Weapon.HOTBAR_SLOT || p.getOpenInventory() != null) {
                inventoryConditionResult.setCancel(true);
            }
        });
    }

    private void handleOpenMenu(PlayerSwapItemEvent event) {
        Player player = event.getPlayer();
        PlayerCharacter pc = mmorpg.getPlayerCharacterManager().getPlayerCharacter(player);
        event.setCancelled(true);
        openMenu(pc);
    }

    private void openMenu(PlayerCharacter pc) {
        Inventory menu = new Inventory(InventoryType.CHEST_1_ROW, "Menu");
        menu.setItemStack(MENU_SKILL_TREE_SLOT, OPEN_SKILL_TREE);
        menu.setItemStack(MENU_QUEST_LOG_SLOT, OPEN_QUEST_LOG);
        menu.setItemStack(MENU_MAP_SLOT, OPEN_MAP);
        menu.setItemStack(MENU_LOGOUT_SLOT, LOGOUT);
        menu.addInventoryCondition((player, slot, clickType, inventoryConditionResult) -> {
            switch (slot) {
                case MENU_SKILL_TREE_SLOT -> openSkillTree(pc);
                case MENU_QUEST_LOG_SLOT -> openQuestLog(pc);
                case MENU_MAP_SLOT -> openMap(pc);
                case MENU_LOGOUT_SLOT -> handleLogoutClick(pc);
            }
            inventoryConditionResult.setCancel(true);
        });
        pc.getPlayer().openInventory(menu);
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        eventHandler.call(new MenuOpenEvent(pc));
    }

    private void openSkillTree(PlayerCharacter pc) {
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        eventHandler.call(new SkillTreeOpenEvent(pc));
        pc.getSkillManager().openSkillTree();
    }

    private void openQuestLog(PlayerCharacter pc) {
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        eventHandler.call(new QuestLogOpenEvent(pc));
        // TODO: actually open
    }

    private void openMap(PlayerCharacter pc) {
        pc.getMapManager().openMap();
        pc.getPlayer().closeInventory();
    }

    private void handleLogoutClick(PlayerCharacter pc) {
        GlobalEventHandler eventHandler = mmorpg.getGlobalEventHandler();
        // PlayerCharacterManager listens to this event.
        eventHandler.call(new ClickMenuLogoutEvent(pc));
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
            eventHandler.call(new ItemConsumeEvent(pc, consumableItem));
            return;
        }
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

        PlayerCharacterMapManager mapManager = pc.getMapManager();
        if (mapManager.isMapOpen()) {
            mapManager.closeMap();
        }

        Weapon weapon = pc.getInventory().getWeapon();
        AutoAttackEvent basicAttackEvent = new AutoAttackEvent(pc, weapon);
        weapon.onAutoAttack().emit(basicAttackEvent);
        MinecraftServer.getGlobalEventHandler().call(basicAttackEvent);
    }

    private void handleInteract(PlayerEntityInteractEvent event) {
        Player player = event.getPlayer();
        PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
        PlayerCharacter pc = pcManager.getPlayerCharacter(player);
        handleInteract(pc);
    }

    private void handleInteract(PlayerUseItemEvent event) {
        Player player = event.getPlayer();
        PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
        PlayerCharacter pc = pcManager.getPlayerCharacter(player);
        handleInteract(pc);
    }

    private void handleInteract(PlayerCharacter pc) {
        PhysicsManager physicsManager = mmorpg.getPhysicsManager();
        Instance instance = pc.getInstance();
        Pos origin = pc.getEyePosition();
        Vec direction = pc.getLookDirection();
        RaycastHit hit = physicsManager.raycast(instance, origin, direction,
                MAX_INTERACTION_DISTANCE, this::isInteractionCollider);
        if (hit != null) {
            InteractCollider collider = (InteractCollider) hit.getCollider();
            collider.interact(pc);
        }
    }

    private void handleBlockInteract(PlayerBlockInteractEvent event) {
        // Prevent doors, trapdoors, etc. from being opened or closed.
        event.setBlockingItemUse(true);
    }

    private void handleItemDrop(ItemDropEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        if (event.getItemStack() == inventory.getItemStack(Weapon.HOTBAR_SLOT)) {
            event.setCancelled(true);
        }
    }

    private boolean isInteractionCollider(Collider collider) {
        return collider instanceof InteractCollider;
    }
}
