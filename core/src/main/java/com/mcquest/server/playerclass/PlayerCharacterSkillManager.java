package com.mcquest.server.playerclass;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.event.SkillUnlockEvent;
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
import net.minestom.server.utils.time.Tick;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

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

    public boolean isUnlocked(@NotNull Skill skill) {
        return unlockedSkills.contains(skill);
    }

    void startCooldown(ActiveSkill skill) {
        cooldowns.put(skill.getId(), skill.getCooldown());
    }

    public Duration getCooldown(@NotNull ActiveSkill skill) {
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
        // TODO: make slots a constant
        for (int slot = 0; slot < 6; slot++) {
            ItemStack itemStack = inventory.getItemStack(slot);
            if (!itemStack.hasTag(PlayerClassManager.SKILL_ID_TAG)) {
                continue;
            }
            int skillId = itemStack.getTag(PlayerClassManager.SKILL_ID_TAG);
            ActiveSkill skill = (ActiveSkill) playerClass.getSkill(skillId);
            inventory.setItemStack(slot, skill.getHotbarItemStack(pc));
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
                                  InventoryConditionResult result) {
        // TODO: maybe need to cancel if player clicks outside inventory
        Skill skill = skillAtSlot(slot);
        if (skill == null) {
            return;
        }

        if (clickType == ClickType.LEFT_CLICK) {
            handleLeftClick(skill, result);
        } else if (clickType == ClickType.START_SHIFT_CLICK) {
            handleShiftClick(skill, result);
        }
    }

    private void handleLeftClick(Skill skill, InventoryConditionResult result) {
        if (!(skill instanceof ActiveSkill)) {
            result.setCancel(true);
            return;
        }

        if (!isUnlocked(skill)) {
            pc.sendMessage(Component.text(skill.getName() + " is locked!",
                    NamedTextColor.RED));
            result.setCancel(true);
            return;
        }

        if (skillOnHotbar(skill)) {
            pc.sendMessage(Component.text(skill.getName()
                    + " is already on your hotbar", NamedTextColor.RED));
            result.setCancel(true);
            return;
        }

        // TODO: may want to add a hotbar itemstack to cursor instead of skill
        //  tree itemstack

        result.setCursorItem(result.getClickedItem());
    }

    private void handleShiftClick(Skill skill, InventoryConditionResult result) {
        result.setCancel(true);

        if (isUnlocked(skill)) {
            return;
        }

        if (skill.getPrerequisite() != null && !isUnlocked(skill.getPrerequisite())) {
            pc.sendMessage(Component.text(skill.getName() + " requires "
                    + skill.getPrerequisite().getName() + "!", NamedTextColor.RED));
            return;
        }

        if (skillPoints == 0) {
            pc.sendMessage(Component.text("No skill points remaining!",
                    NamedTextColor.RED));
            return;
        }

        unlockSkill(skill);
        openSkillTree();
    }

    private Skill skillAtSlot(int slot) {
        for (Skill skill : pc.getPlayerClass().getSkills()) {
            if (slot == 9 * skill.getSkillTreeRow() + skill.getSkillTreeColumn()) {
                return skill;
            }
        }
        return null;
    }

    // TODO: should maybe replace this with some hotbar abstraction
    private boolean skillOnHotbar(Skill skill) {
        // TODO: slots should be a constant
        PlayerInventory inventory = pc.getPlayer().getInventory();
        for (int slot = 0; slot < 6; slot++) {
            ItemStack itemStack = inventory.getItemStack(slot);
            if (itemStack.hasTag(PlayerClassManager.SKILL_ID_TAG) &&
                    itemStack.getTag(PlayerClassManager.SKILL_ID_TAG) == skill.getId()) {
                return true;
            }
        }
        return false;
    }
}
