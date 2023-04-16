package com.mcquest.server.playerclass;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.event.AddSkillToHotbarEvent;
import com.mcquest.server.event.SkillUnlockEvent;
import com.mcquest.server.util.Sounds;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.inventory.condition.InventoryConditionResult;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.utils.time.Tick;
import org.jetbrains.annotations.ApiStatus;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PlayerCharacterSkillManager {
    private final PlayerCharacter pc;
    private int skillPoints;
    private final Set<Skill> unlockedSkills;
    private final Map<Integer, Duration> cooldowns;

    public PlayerCharacterSkillManager(PlayerCharacter pc, int skillPoints) {
        this.pc = pc;
        this.skillPoints = skillPoints;
        unlockedSkills = new HashSet<>();
        cooldowns = new HashMap<>();
    }

    public boolean isUnlocked(Skill skill) {
        return unlockedSkills.contains(skill);
    }

    void startCooldown(ActiveSkill skill) {
        cooldowns.put(skill.getId(), skill.getCooldown());
    }

    public Duration getCooldown(ActiveSkill skill) {
        return cooldowns.getOrDefault(skill.getId(), Duration.ZERO);
    }

    void tickSkillCooldowns() {
        Duration tick = Tick.server(1);
        cooldowns.replaceAll((skillId, cooldown) -> cooldown.minus(tick));
        cooldowns.values().removeIf(cooldown -> cooldown.isNegative() || cooldown.isZero());
        updateHotbar();
    }

    private void updateHotbar() {
        PlayerClass playerClass = pc.getPlayerClass();
        PlayerInventory inventory = pc.getPlayer().getInventory();
        for (int i = 0; i < 8; i++) {
            if (true) continue;
            ItemStack itemStack = inventory.getItemStack(i);
            if (!itemStack.hasTag(PlayerClassManager.SKILL_ID_TAG)) {
                continue;
            }
            int skillId = itemStack.getTag(PlayerClassManager.SKILL_ID_TAG);
            ActiveSkill skill = (ActiveSkill) playerClass.getSkill(skillId);
            Duration cooldown = cooldowns.get(skill.getId());
            double currentMillis = cooldown.toMillis();
            double totalMillis = skill.getCooldown().toMillis();
            int cooldownDivision = (int) Math.ceil(currentMillis / totalMillis);
            Material material = Material.fromNamespaceId(""); // TODO
            ItemStack newItemStack = itemStack.withMaterial(material).withMeta(builder -> builder.customModelData(1));
            inventory.setItemStack(i, newItemStack);
        }
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    @ApiStatus.Internal
    public void grantSkillPoint() {
        skillPoints++;
    }

    private void unlockSkill(Skill skill) {
        skillPoints--;
        unlockedSkills.add(skill);
        pc.sendMessage(Component.text("Unlocked " + skill.getName(), NamedTextColor.GREEN));
        SkillUnlockEvent event = new SkillUnlockEvent(pc, skill);
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.call(event);
    }

    public void openSkillTree() {
        Inventory menu = makeSkillTreeMenu();
        pc.getPlayer().openInventory(menu);
    }

    private Inventory makeSkillTreeMenu() {
        PlayerClass playerClass = pc.getPlayerClass();
        String title = playerClass.getName() + " Skill Tree (" + skillPoints + " points)";
        Inventory inventory = new Inventory(InventoryType.CHEST_6_ROW, title);
        for (Skill skill : playerClass.getSkills()) {
            ItemStack itemStack = skill.getSkillTreeItemStack(pc);
            inventory.setItemStack(9 * skill.getSkillTreeRow() + skill.getSkillTreeColumn(), itemStack);
        }
        inventory.addInventoryCondition(this::handleSkillClick);
        return inventory;
    }

    private void handleSkillClick(Player player, int slot, ClickType clickType,
                                  InventoryConditionResult inventoryConditionResult) {
        inventoryConditionResult.setCancel(true);
        Skill skill = skillAtSlot(slot);
        if (skill == null) {
            return;
        }
        pc.getPlayer().playSound(Sounds.CLICK);
        boolean isUnlocked = skill.isUnlocked(pc);
        if (clickType == ClickType.LEFT_CLICK) {
            if (!(skill instanceof ActiveSkill activeSkill)) {
                return;
            }
            if (isUnlocked) {
                PlayerInventory inventory = pc.getPlayer().getInventory();
                int hotbarSlot = firstEmptyHotbarSlot(inventory);
                if (hotbarSlot == -1) {
                    pc.sendMessage(Component.text(
                            "No room on hotbar", NamedTextColor.RED));
                } else if (skillOnHotbar(skill, inventory)) {
                    pc.sendMessage(Component.text(
                            skill.getName() + " is already on your hotbar", NamedTextColor.RED));
                } else {
                    ItemStack hotbarItemStack = ((ActiveSkill) skill).getHotbarItemStack();
                    inventory.addItemStack(hotbarItemStack);
                    pc.sendMessage(Component.text(
                            "Added " + skill.getName() + " to hotbar", NamedTextColor.GREEN));
                    AddSkillToHotbarEvent event = new AddSkillToHotbarEvent(pc, activeSkill, hotbarSlot);
                    MinecraftServer.getGlobalEventHandler().call(event);
                }
            } else {
                pc.sendMessage(
                        Component.text(skill.getName() + " is not unlocked", NamedTextColor.RED));
            }
        } else if (clickType == ClickType.START_SHIFT_CLICK) {
            if (isUnlocked) {
                pc.sendMessage(Component.text(
                        skill.getName() + " is already unlocked", NamedTextColor.RED));
            } else if (skillPoints == 0) {
                pc.sendMessage(Component.text("No skill points available", NamedTextColor.RED));
            } else {
                unlockSkill(skill);
                // Rerender.
                openSkillTree();
            }
        }
    }

    private Skill skillAtSlot(int slot) {
        for (Skill skill : pc.getPlayerClass().getSkills()) {
            if (slot == 9 * skill.getSkillTreeRow() + skill.getSkillTreeColumn()) {
                return skill;
            }
        }
        return null;
    }

    private static int firstEmptyHotbarSlot(PlayerInventory inventory) {
        for (int i = 0; i < 8; i++) {
            ItemStack itemStack = inventory.getItemStack(i);
            if (itemStack.isAir()) {
                return i;
            }
        }
        return -1;
    }

    private static boolean skillOnHotbar(Skill skill, PlayerInventory inventory) {
        for (int i = 0; i < 8; i++) {
            ItemStack itemStack = inventory.getItemStack(i);
            if (itemStack.hasTag(PlayerClassManager.SKILL_ID_TAG) &&
                    itemStack.getTag(PlayerClassManager.SKILL_ID_TAG) == skill.getId()) {
                return true;
            }
        }
        return false;
    }
}
