package net.mcquest.core.ui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.cartography.MapViewer;
import net.mcquest.core.character.CharacterHitbox;
import net.mcquest.core.character.NonPlayerCharacter;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.character.PlayerCharacterManager;
import net.mcquest.core.event.AutoAttackEvent;
import net.mcquest.core.event.MenuOpenEvent;
import net.mcquest.core.event.QuestLogOpenEvent;
import net.mcquest.core.event.SkillTreeOpenEvent;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.item.PlayerCharacterInventory;
import net.mcquest.core.item.Weapon;
import net.mcquest.core.physics.Collider;
import net.mcquest.core.physics.PhysicsManager;
import net.mcquest.core.physics.RaycastHit;
import net.mcquest.core.util.ItemStackUtility;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.item.ItemDropEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerHandAnimationEvent;
import net.minestom.server.event.player.PlayerSwapItemEvent;
import net.minestom.server.event.player.PlayerUseItemEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
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
    private static final ItemStack OPEN_SKILL_TREE = ItemStackUtility.create(
            Material.IRON_SWORD,
            Component.text("Skill Tree", NamedTextColor.YELLOW),
            List.of(CLICK_TO_OPEN)
    ).build();
    private static final ItemStack OPEN_MAP = ItemStackUtility.create(
            Material.FILLED_MAP,
            Component.text("Map", NamedTextColor.YELLOW),
            List.of(CLICK_TO_OPEN)
    ).build();
    private static final ItemStack OPEN_QUEST_LOG = ItemStackUtility.create(
            Material.BOOK,
            Component.text("Quest Log", NamedTextColor.YELLOW),
            List.of(CLICK_TO_OPEN)
    ).build();
    private static final ItemStack LOGOUT = ItemStackUtility.create(
            Material.BARRIER,
            Component.text("Logout", NamedTextColor.YELLOW),
            List.of(Component.text("Click to logout", NamedTextColor.GRAY))
    ).build();

    private final Mmorpg mmorpg;

    public InteractionHandler(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
    }

    public void registerListeners() {
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PickupItemEvent.class, this::handlePickupItem);
        eventHandler.addListener(PlayerSwapItemEvent.class, this::handleOpenMenu);
        eventHandler.addListener(PlayerHandAnimationEvent.class, this::handleBasicAttack);
        eventHandler.addListener(PlayerUseItemEvent.class, this::handleInteract);
        eventHandler.addListener(PlayerBlockInteractEvent.class, this::handleBlockInteract);
        eventHandler.addListener(ItemDropEvent.class, this::handleItemDrop);
    }

    private void handleOpenMenu(PlayerSwapItemEvent event) {
        Player player = event.getPlayer();
        PlayerCharacter pc = mmorpg.getPlayerCharacterManager().getPlayerCharacter(player);

        if (pc == null) {
            return;
        }

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
        pc.getEntity().openInventory(menu);
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
        pc.getMapViewer().open();
        pc.getEntity().closeInventory();
    }

    private void handleLogoutClick(PlayerCharacter pc) {
        mmorpg.getPlayerCharacterManager().logoutPlayerCharacter(pc);
        mmorpg.getLoginManager().sendToLogin(pc.getEntity());
    }

    private void handlePickupItem(PickupItemEvent event) {
        if (event.getEntity() instanceof Player player) {
            PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
            PlayerCharacter pc = pcManager.getPlayerCharacter(player);
            // TODO
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

        MapViewer mapManager = pc.getMapViewer();
        if (mapManager.isOpen()) {
            mapManager.close();
        }

        Weapon weapon = pc.getInventory().getWeapon();
        AutoAttackEvent basicAttackEvent = new AutoAttackEvent(pc, weapon);
        weapon.onAutoAttack().emit(basicAttackEvent);
        MinecraftServer.getGlobalEventHandler().call(basicAttackEvent);
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
            if (hit.getCollider() instanceof InteractCollider interactCollider) {
                interactCollider.interact(pc);
            } else if (hit.getCollider() instanceof CharacterHitbox hitbox) {
                ((NonPlayerCharacter) hitbox.getCharacter()).interact(pc);
            }
        }
    }

    private void handleBlockInteract(PlayerBlockInteractEvent event) {
        // Prevent doors, trapdoors, etc. from being opened or closed.
        event.setBlockingItemUse(true);
    }

    private void handleItemDrop(ItemDropEvent event) {
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        if (event.getItemStack() == inventory.getItemStack(PlayerCharacterInventory.WEAPON_SLOT)) {
            event.setCancelled(true);
        }
    }

    private boolean isInteractionCollider(Collider collider) {
        return collider instanceof InteractCollider ||
                collider instanceof CharacterHitbox hitbox && hitbox.getCharacter() instanceof NonPlayerCharacter;
    }
}
