package net.mcquest.core.login;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.persistence.PlayerCharacterData;
import net.mcquest.core.playerclass.PlayerClass;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

class Menus {
    static Inventory characterSelectMenu(PlayerCharacterData[] data, Mmorpg mmorpg) {
        Inventory inventory = new Inventory(InventoryType.CHEST_1_ROW, "Select character");

        for (int characterSlot = 0; characterSlot < data.length; characterSlot++) {
            ItemStack button = Buttons.selectCharacter(characterSlot, data[characterSlot], mmorpg);
            inventory.setItemStack(characterSlot * 2, button);
        }

        inventory.setItemStack(8, Buttons.deleteCharactersMenu());

        inventory.addInventoryCondition((player, invSlot, clickType, result) -> {
            result.setCancel(true);

            if (invSlot % 2 == 1) {
                return;
            }

            if (invSlot == 8) {
                player.openInventory(deleteCharacterMenu(data, mmorpg));
                return;
            }

            int characterSlot = invSlot / 2;
            PlayerCharacterData datum = data[characterSlot];
            if (datum == null) {
                player.openInventory(createCharacterMenu(characterSlot, mmorpg));
            } else {
                player.closeInventory();
                player.setInvisible(false);
                mmorpg.getLoginManager().loginPlayerCharacter(player, characterSlot, datum);
            }
        });

        return inventory;
    }

    static Inventory createCharacterMenu(int characterSlot, Mmorpg mmorpg) {
        Inventory inventory = new Inventory(InventoryType.CHEST_1_ROW, "Create character");

        List<PlayerClass> playerClasses = new ArrayList<>(mmorpg.getPlayerClassManager().getPlayerClasses());

        for (int i = 0; i < playerClasses.size(); i++) {
            PlayerClass playerClass = playerClasses.get(i);
            inventory.setItemStack(i * 2, Buttons.selectClass(playerClass));
        }

        inventory.setItemStack(8, Buttons.goBack());

        inventory.addInventoryCondition((player, slot, clickType, result) -> {
            result.setCancel(true);

            if (slot % 2 == 1) {
                return;
            }

            if (slot == 8) {
                mmorpg.getLoginManager().openCharacterSelectMenu(player);
            }

            int playerClassIndex = slot / 2;
            if (playerClassIndex < playerClasses.size()) {
                PlayerClass playerClass = playerClasses.get(playerClassIndex);
                mmorpg.getLoginManager().createCharacter(player, characterSlot, playerClass);
            }
        });

        return inventory;
    }

    static Inventory deleteCharacterMenu(PlayerCharacterData[] data, Mmorpg mmorpg) {
        Inventory inventory = new Inventory(InventoryType.CHEST_1_ROW, "Delete character");

        for (int characterSlot = 0; characterSlot < data.length; characterSlot++) {
            if (data[characterSlot] == null) {
                continue;
            }

            ItemStack button = Buttons.deleteCharacter(characterSlot);
            inventory.setItemStack(characterSlot * 2, button);
        }

        inventory.setItemStack(8, Buttons.goBack());

        inventory.addInventoryCondition((player, slot, clickType, result) -> {
            result.setCancel(true);

            if (slot % 2 == 1) {
                return;
            }

            if (slot == 8) {
                mmorpg.getLoginManager().openCharacterSelectMenu(player);
                return;
            }

            int characterSlot = slot / 2;
            if (data[characterSlot] == null) {
                return;
            }

            mmorpg.getLoginManager().deleteCharacter(player, characterSlot);
        });

        return inventory;
    }
}
