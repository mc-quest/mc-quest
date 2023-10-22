package net.mcquest.core.login;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.persistence.PlayerCharacterData;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;

public class LoginManager {
    public static final int NUM_CHARACTERS = 4;

    private final Mmorpg mmorpg;

    public LoginManager(Mmorpg mmorpg) {
        this.mmorpg = mmorpg;
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerLoginEvent.class, this::handleLogin);
    }

    private void handleLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        PlayerCharacterData[] data = mmorpg.getPersistenceService().fetch(player.getUuid());

        if (data.length != NUM_CHARACTERS) {
            throw new RuntimeException();
        }

        Inventory menu = createCharacterSelectMenu(data);
        player.openInventory(menu);
        // TODO: prevent closing inventory
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
                mmorpg.getPlayerCharacterManager().loginPlayerCharacter(player, slot, datum);
            }
        });

        return inventory;
    }
}
