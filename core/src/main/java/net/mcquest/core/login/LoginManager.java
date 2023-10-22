package net.mcquest.core.login;

import net.kyori.adventure.text.Component;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.persistence.PlayerCharacterData;
import net.mcquest.core.util.ItemStackUtility;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Point;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.coordinate.Vec;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.inventory.InventoryCloseEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.Collections;

public class LoginManager {
    public static final int NUM_CHARACTERS = 4;

    private final Mmorpg mmorpg;
    private final Instance loginInstance;

    public LoginManager(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;

        loginInstance = createLoginInstance();

        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerLoginEvent.class, this::handleLogin);
        eventHandler.addListener(PlayerSpawnEvent.class, this::handleSpawn);

        loginInstance.eventNode().addListener(InventoryCloseEvent.class, this::handleInventoryClose);
    }

    private Instance createLoginInstance() {
        Instance instance = MinecraftServer.getInstanceManager().createInstanceContainer();
        for (Point position : new Point[]{
                new Vec(-1, 0, -1),
                new Vec(-1, 0, 0),
                new Vec(-1, 0, 1),
                new Vec(0, 0, -1),
                new Vec(0, 0, 0),
                new Vec(0, 0, 1),
                new Vec(1, 0, -1),
                new Vec(1, 0, 0),
                new Vec(1, 0, 1),
        }) {
            instance.setBlock(position, Block.BARRIER);
        }
        return instance;
    }

    private void handleLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        player.setRespawnPoint(new Pos(0, 1, 0));
        player.setGameMode(GameMode.ADVENTURE);
        event.setSpawningInstance(loginInstance);
    }

    private void handleSpawn(PlayerSpawnEvent event) {
        if (event.getSpawnInstance() != loginInstance) {
            return;
        }

        Player player = event.getPlayer();
        PlayerCharacterData[] data = mmorpg.getPersistenceService().fetch(player.getUuid());
        Inventory menu = createCharacterSelectMenu(data);
        player.openInventory(menu);
        // TODO: prevent closing inventory
    }

    private void handleInventoryClose(InventoryCloseEvent event) {
        event.setNewInventory(event.getInventory());
    }

    private Inventory createCharacterSelectMenu(PlayerCharacterData[] data) {
        Inventory inventory = new Inventory(InventoryType.CHEST_1_ROW, "Select Character");
        for (int slot = 0; slot < NUM_CHARACTERS; slot++) {
            ItemStack button = createCharacterSelectButton(data[slot]);
            inventory.setItemStack(slot * 2, button);
        }

        inventory.addInventoryCondition((player, invSlot, clickType, result) -> {
            if (invSlot % 2 != 0) {
                return;
            }

            if (invSlot == 8) {
                // TODO delete character
            }

            int slot = invSlot / 2;
            PlayerCharacterData datum = data[slot];
            if (datum == null) {
                // TODO: open create new character menu
            } else {
                player.closeInventory();
                mmorpg.getPlayerCharacterManager().loginPlayerCharacter(player, slot, datum);
            }
        });

        return inventory;
    }

    private ItemStack createCharacterSelectButton(PlayerCharacterData data) {
        if (data == null) {
            return ItemStackUtility.create(
                    Material.EMERALD,
                    Component.text("Create new character"),
                    Collections.emptyList()
            ).build();
        }

        return ItemStackUtility.create(
                Material.IRON_SWORD,
                Component.text(data.playerClassId()),
                Collections.emptyList()
        ).build();
    }
}
