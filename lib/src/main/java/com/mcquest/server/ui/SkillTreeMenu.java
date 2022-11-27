package com.mcquest.server.ui;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.playerclass.ActiveSkill;
import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.playerclass.Skill;
import com.mcquest.server.playerclass.SkillTreeDecoration;
import com.mcquest.server.util.ItemStackUtility;
import com.mcquest.server.util.Sounds;
import com.mcquest.server.util.TextUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.inventory.condition.InventoryConditionResult;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

class SkillTreeMenu {
    static void open(PlayerCharacter pc) {
        PlayerClass playerClass = pc.getPlayerClass();
        Inventory inventory = new Inventory(InventoryType.CHEST_6_ROW, "Skill Tree");
        for (Skill skill : playerClass.getSkills()) {
            ItemStack itemStack = ItemStackUtility.createItemStack(Material.SHIELD, Component.text("Bash", NamedTextColor.GREEN), TextUtility.wordWrap(skill.getDescription()));
            inventory.setItemStack(9 * skill.getSkillTreeRow() + skill.getSkillTreeColumn(), itemStack);
        }
        for (int i = 0; i < playerClass.getSkillTreeDecorationCount(); i++) {
            SkillTreeDecoration decoration = playerClass.getSkillTreeDecoration(i);

        }
        inventory.addInventoryCondition((Player player, int slot, ClickType clickType,
                                         InventoryConditionResult inventoryConditionResult) -> {
            handleSkillClick(pc, inventory, slot, clickType);
            inventoryConditionResult.setCancel(true);
        });
        pc.getPlayer().openInventory(inventory);
    }

    private static void handleSkillClick(PlayerCharacter pc, Inventory inventory, int slot, ClickType clickType) {
        Skill skill = null; // TODO
        if (skill == null) {
            return;
        }
        pc.getPlayer().playSound(Sounds.CLICK);
        boolean isUnlocked = skill.isUnlocked(pc);
        if (clickType == ClickType.LEFT_CLICK) {
            if (!(skill instanceof ActiveSkill)) {
                return;
            }
            if (isUnlocked) {
                PlayerInventory playerInventory = pc.getPlayer().getInventory();
                if (roomOnHotbar(playerInventory)) {
                    ItemStack hotbarItemStack = ((ActiveSkill) skill).getHotbarItemStack();
                    playerInventory.addItemStack(hotbarItemStack);
                    pc.sendMessage(Component.text(
                            "Added " + skill.getName() + " to hotbar", NamedTextColor.GREEN));
                } else {
                    pc.sendMessage(Component.text(
                            "No room on hotbar", NamedTextColor.RED
                    ));
                }
            } else {
                pc.sendMessage(
                        Component.text(skill.getName() + " is not unlocked", NamedTextColor.RED));
            }
        } else if (clickType == ClickType.SHIFT_CLICK) {
            if (isUnlocked) {
                pc.sendMessage(Component.text(
                        skill.getName() + " is already unlocked", NamedTextColor.RED));
            } else {
                pc.getSkillManager().unlockSkill(skill);
                // Rerender skill in skill tree.
                inventory.setItemStack(slot, skill.getSkillTreeItemStack(pc));
            }
        }
    }

    private static boolean roomOnHotbar(PlayerInventory inventory) {
        for (int slot = 0; slot < 9; slot++) {
            ItemStack itemStack = inventory.getItemStack(slot);
            if (itemStack.isAir()) {
                return true;
            }
        }
        return false;
    }
}
