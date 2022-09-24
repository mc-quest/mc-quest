package com.mcquest.server.ui;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.playerclass.Skill;
import com.mcquest.server.playerclass.SkillTreeDecoration;
import com.mcquest.server.util.ItemStackUtility;
import com.mcquest.server.util.TextUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

class SkillTreeMenu {
    static void open(PlayerCharacter pc) {
        PlayerClass playerClass = pc.getPlayerClass();
        Player player = pc.getPlayer();
        Inventory inventory = new Inventory(InventoryType.CHEST_6_ROW, "Skill Tree");
        for (Skill skill : playerClass.getSkills()) {
            ItemStack itemStack = ItemStackUtility.createItemStack(Material.SHIELD, Component.text("Bash", NamedTextColor.GREEN), TextUtility.wordWrap(skill.getDescription()));
            inventory.setItemStack(9 * skill.getSkillTreeRow() + skill.getSkillTreeColumn(), itemStack);
        }
        for (int i = 0; i < playerClass.getSkillTreeDecorationCount(); i++) {
            SkillTreeDecoration decoration = playerClass.getSkillTreeDecoration(i);

        }
        player.openInventory(inventory);
    }
}
