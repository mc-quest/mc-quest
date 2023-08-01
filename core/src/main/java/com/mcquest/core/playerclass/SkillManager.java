package com.mcquest.core.playerclass;

import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.event.SkillAddToHotbarEvent;
import com.mcquest.core.event.SkillRemoveFromHotbarEvent;
import com.mcquest.core.event.SkillUnlockEvent;
import com.mcquest.core.persistence.PlayerCharacterData;
import com.mcquest.core.resourcepack.CustomModelData;
import com.mcquest.core.resourcepack.Materials;
import com.mcquest.core.util.ItemStackUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
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
import net.minestom.server.tag.Tag;
import net.minestom.server.utils.time.Tick;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.*;

public class SkillManager {
    static final Tag<Integer> SKILL_ID_TAG = Tag.Integer("skill_id");
    public static final int MIN_HOTBAR_SLOT = 0;
    public static final int MAX_HOTBAR_SLOT = 5;

    private final PlayerCharacter pc;
    private final Set<Skill> unlockedSkills;
    private final Map<ActiveSkill, Duration> cooldowns;
    private int skillPoints;

    public SkillManager(PlayerCharacter pc, PlayerCharacterData data) {
        this.pc = pc;

        PlayerClass playerClass = pc.getPlayerClass();

        unlockedSkills = new HashSet<>();
        for (int skillId : new int[0]) { // TODO: data.getUnlockedSkillIds()
            Skill skill = playerClass.getSkill(skillId);
            unlockedSkills.add(skill);
        }

        cooldowns = new HashMap<>();
        for (Map.Entry<Integer, Long> cooldown : new HashMap<Integer, Long>().entrySet()) { // TODO: data.skillCooldowns
            int skillId = cooldown.getKey();
            ActiveSkill skill = (ActiveSkill) playerClass.getSkill(skillId);
            cooldowns.put(skill, Duration.ofMillis(0));
        }

        PlayerInventory inventory = pc.getPlayer().getInventory();

        Integer[] hotbarSkillIds = new Integer[MAX_HOTBAR_SLOT - MIN_HOTBAR_SLOT + 1]; // TODO data.getHotbarSkills()
        for (int slot = MIN_HOTBAR_SLOT; slot <= MAX_HOTBAR_SLOT; slot++) {
            Integer skillId = hotbarSkillIds[slot];
            if (skillId == null) {
                inventory.setItemStack(slot, hotbarSkillPlaceholder(slot, false));
            } else {
                ActiveSkill skill = (ActiveSkill) playerClass.getSkill(skillId);
                inventory.setItemStack(slot, skill.getHotbarItemStack(pc));
            }
        }

        skillPoints = data.getSkillPoints();

        inventory.addInventoryCondition(this::handleInventoryClick);
    }

    private ItemStack hotbarSkillPlaceholder(int slot, boolean flashing) {
        String nameContent = String.format("Active Skill Slot %d", slot + 1);
        TextComponent displayName = Component.text(nameContent, NamedTextColor.YELLOW);
        List<TextComponent> lore = List.of(Component.text("Active skills go here"));
        return ItemStackUtility.create(Materials.GUI, displayName, lore)
                .meta(builder -> builder.customModelData(CustomModelData.HOTBAR_SKILL_PLACEHOLDER))
                .build();
    }

    private void handleInventoryClick(Player player, int slot, ClickType clickType,
                                      InventoryConditionResult result) {
        PlayerInventory inventory = player.getInventory();
        ItemStack cursor = result.getCursorItem();

        if (cursor.isAir()) {
            if (!(slot >= MIN_HOTBAR_SLOT && slot <= MAX_HOTBAR_SLOT)) {
                return;
            }

            if (clickType == ClickType.START_SHIFT_CLICK) {
                result.setCancel(true);
                return;
            }

            ItemStack clicked = inventory.getItemStack(slot);
            ActiveSkill clickedSkill = (ActiveSkill) getSkill(clicked);

            if (clickedSkill == null) {
                // Placeholder slot.
                result.setCancel(true);
                return;
            }

            result.setClickedItem(clickedSkill.getCursorItemStack());
            result.setCursorItem(hotbarSkillPlaceholder(slot, false));
            SkillRemoveFromHotbarEvent event = new SkillRemoveFromHotbarEvent(pc, clickedSkill, slot);
            MinecraftServer.getGlobalEventHandler().call(event);
        } else {
            ActiveSkill cursorSkill = (ActiveSkill) getSkill(cursor);

            if (cursorSkill == null) {
                if (slot >= MIN_HOTBAR_SLOT && slot <= MAX_HOTBAR_SLOT) {
                    result.setCancel(true);
                }
                return;
            }

            if (!(slot >= MIN_HOTBAR_SLOT && slot <= MAX_HOTBAR_SLOT)) {
                if (slot != -999) {
                    result.setCursorItem(result.getClickedItem());
                    result.setClickedItem(ItemStack.AIR);
                    inventory.update();
                }
                return;
            }

            GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();

            ItemStack clicked = inventory.getItemStack(slot);
            ActiveSkill clickedSkill = (ActiveSkill) getSkill(clicked);

            if (clickedSkill == null) {
                result.setClickedItem(ItemStack.AIR);
            } else {
                eventHandler.call(new SkillRemoveFromHotbarEvent(pc, clickedSkill, slot));
                result.setClickedItem(clickedSkill.getCursorItemStack());
            }

            result.setCursorItem(cursorSkill.getHotbarItemStack(pc));
            eventHandler.call(new SkillAddToHotbarEvent(pc, cursorSkill, slot));
        }
    }

    public boolean isUnlocked(@NotNull Skill skill) {
        return unlockedSkills.contains(skill);
    }

    public Duration getCooldown(@NotNull ActiveSkill skill) {
        return cooldowns.getOrDefault(skill, Duration.ZERO);
    }

    void startCooldown(ActiveSkill skill) {
        cooldowns.put(skill, skill.getCooldown());
    }

    void tickSkillCooldowns() {
        Duration tick = Tick.server(1);
        cooldowns.replaceAll((skill, cooldown) -> cooldown.minus(tick));
        cooldowns.values().removeIf(cooldown -> cooldown.isNegative() || cooldown.isZero());
        updateHotbar();
    }

    private void updateHotbar() {
        PlayerInventory inventory = pc.getPlayer().getInventory();
        for (int slot = MIN_HOTBAR_SLOT; slot <= MAX_HOTBAR_SLOT; slot++) {
            ItemStack itemStack = inventory.getItemStack(slot);
            ActiveSkill skill = (ActiveSkill) getSkill(itemStack);
            if (skill != null) {
                inventory.setItemStack(slot, skill.getHotbarItemStack(pc));
            }
        }
    }

    public int getSkillPoints() {
        return skillPoints;
    }

    @ApiStatus.Internal
    public void grantSkillPoint() {
        skillPoints++;
    }

    void unlockSkill(Skill skill) {
        skillPoints--;
        unlockedSkills.add(skill);
        pc.sendMessage(skillUnlockedMessage(skill));
        SkillUnlockEvent event = new SkillUnlockEvent(pc, skill);
        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.call(event);
    }

    public void openSkillTree() {
        pc.getPlayer().openInventory(new SkillTreeMenu());
    }

    private @Nullable ActiveSkill getHotbarSkill(int slot) {
        if (!(slot >= MIN_HOTBAR_SLOT && slot <= MAX_HOTBAR_SLOT)) {
            throw new IllegalArgumentException();
        }

        PlayerInventory inventory = pc.getPlayer().getInventory();
        ItemStack itemStack = inventory.getItemStack(slot);
        return (ActiveSkill) getSkill(itemStack);
    }

    private boolean isSkillOnHotbar(Skill skill) {
        for (int slot = MIN_HOTBAR_SLOT; slot <= MAX_HOTBAR_SLOT; slot++) {
            if (getHotbarSkill(slot) == skill) {
                return true;
            }
        }
        return false;
    }

    Skill getSkill(ItemStack itemStack) {
        if (!itemStack.hasTag(SKILL_ID_TAG)) {
            return null;
        }

        int skillId = itemStack.getTag(SKILL_ID_TAG);
        return pc.getPlayerClass().getSkill(skillId);
    }

    private TextComponent skillTreeTitle() {
        String content = String.format(
                "%s Skill Tree (%d point%s)",
                pc.getPlayerClass().getName(),
                skillPoints,
                skillPoints == 1 ? "" : "s"
        );
        return Component.text(content);
    }

    private static TextComponent skillUnlockedMessage(Skill skill) {
        String content = String.format("Unlocked %s!", skill.getName());
        return Component.text(content, NamedTextColor.GREEN);
    }

    private static TextComponent alreadyUnlockedMessage(Skill skill) {
        String content = String.format("%s is already unlocked!", skill.getName());
        return Component.text(content, NamedTextColor.RED);
    }

    private static TextComponent noSkillPointsMessage() {
        return Component.text("No skill points remaining!", NamedTextColor.RED);
    }

    private static TextComponent requiresLevelMessage(Skill skill) {
        String content = String.format("%s requires level %d!", skill.getName(), skill.getLevel());
        return Component.text(content, NamedTextColor.RED);
    }

    private static TextComponent requiresPrereqMessage(Skill skill) {
        Skill prereq = skill.getPrerequisite();
        String content = String.format("%s requires %s!", skill.getName(), prereq.getName());
        return Component.text(content, NamedTextColor.RED);
    }

    private static TextComponent notUnlockedMessage(ActiveSkill skill) {
        String content = String.format("%s is not unlocked!", skill.getName());
        return Component.text(content, NamedTextColor.RED);
    }

    private static TextComponent alreadyOnHotbarMessage(ActiveSkill skill) {
        String content = String.format("%s is already on your hotbar!", skill.getName());
        return Component.text(content, NamedTextColor.RED);
    }

    private class SkillTreeMenu extends Inventory {
        private SkillTreeMenu() {
            super(InventoryType.CHEST_6_ROW, skillTreeTitle());
            addInventoryCondition(this::handleClick);
            for (Skill skill : pc.getPlayerClass().getSkills()) {
                setItemStack(skill.getSkillTreeSlot(), skill.getSkillTreeItemStack(pc));
            }
        }

        private void handleClick(Player player, int slot, ClickType clickType,
                                 InventoryConditionResult result) {
            if (!result.getCursorItem().isAir()) {
                result.setCancel(true);
            } else if (clickType == ClickType.START_SHIFT_CLICK) {
                handleShiftClick(slot, result);
            } else if (clickType == ClickType.LEFT_CLICK) {
                handleLeftClick(slot, result);
            } else {
                result.setCancel(true);
            }

            update(player);
        }

        private void handleShiftClick(int slot, InventoryConditionResult result) {
            result.setCancel(true);

            ItemStack clicked = getItemStack(slot);
            Skill clickedSkill = getSkill(clicked);

            if (clickedSkill == null) {
                return;
            }

            if (isUnlocked(clickedSkill)) {
                pc.sendMessage(alreadyUnlockedMessage(clickedSkill));
                return;
            }

            if (skillPoints == 0) {
                pc.sendMessage(noSkillPointsMessage());
                return;
            }

            if (pc.getLevel() < clickedSkill.getLevel()) {
                pc.sendMessage(requiresLevelMessage(clickedSkill));
                return;
            }

            Skill prereq = clickedSkill.getPrerequisite();
            if (prereq != null && !prereq.isUnlocked(pc)) {
                pc.sendMessage(requiresPrereqMessage(clickedSkill));
                return;
            }

            unlockSkill(clickedSkill);

            setTitle(skillTreeTitle());
            setItemStack(slot, clickedSkill.getSkillTreeItemStack(pc));
            for (Skill skill : pc.getPlayerClass().getSkills()) {
                if (skill.getPrerequisite() == clickedSkill) {
                    setItemStack(skill.getSkillTreeSlot(), skill.getSkillTreeItemStack(pc));
                }
            }
        }

        private void handleLeftClick(int slot, InventoryConditionResult result) {
            ItemStack clicked = getItemStack(slot);
            Skill clickedSkill = getSkill(clicked);

            if (!(clickedSkill instanceof ActiveSkill activeSkill)) {
                result.setCancel(true);
                return;
            }

            if (!clickedSkill.isUnlocked(pc)) {
                pc.sendMessage(notUnlockedMessage(activeSkill));
                result.setCancel(true);
                return;
            }

            if (isSkillOnHotbar(activeSkill)) {
                pc.sendMessage(alreadyOnHotbarMessage(activeSkill));
                result.setCancel(true);
                return;
            }

            result.setClickedItem(activeSkill.getCursorItemStack());
            result.setCursorItem(clickedSkill.getSkillTreeItemStack(pc));
        }
    }
}
