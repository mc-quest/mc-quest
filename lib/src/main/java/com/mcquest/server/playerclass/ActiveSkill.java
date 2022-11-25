package com.mcquest.server.playerclass;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.event.EventEmitter;
import com.mcquest.server.event.PlayerCharacterUseActiveSkillEvent;
import com.mcquest.server.util.ItemStackUtility;
import com.mcquest.server.util.TextUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ActiveSkill extends Skill {
    private final double manaCost;
    private final Duration cooldown;
    private final EventEmitter<PlayerCharacterUseActiveSkillEvent> onUse;

    ActiveSkill(int id, String name, int level, @Nullable Integer prerequisiteId,
                Material icon, String description, int skillTreeRow,
                int skillTreeColumn, double manaCost, Duration cooldown) {
        super(id, name, level, prerequisiteId, icon, description, skillTreeRow, skillTreeColumn);
        this.manaCost = manaCost;
        this.cooldown = cooldown;
        this.onUse = new EventEmitter<>();
    }

    public double getManaCost() {
        return manaCost;
    }

    public Duration getCooldown() {
        return cooldown;
    }

    public EventEmitter<PlayerCharacterUseActiveSkillEvent> onUse() {
        return onUse;
    }

    public Duration getCooldown(PlayerCharacter pc) {
        return pc.getSkillManager().getCooldown(this);
    }

    @Override
    public ItemStack getSkillTreeItemStack(PlayerCharacter pc) {
        boolean isUnlocked = isUnlocked(pc);
        Material icon = isUnlocked ? getIcon() : Material.BARRIER;
        Component displayName = Component.text(getName(), NamedTextColor.YELLOW);
        List<TextComponent> lore = new ArrayList<>();
        lore.add(Component.text("Active Skill", NamedTextColor.GRAY));
        lore.add(Component.text("Mana Cost: " + manaCost));
        int cooldownSeconds = cooldown.toSecondsPart();
        lore.add(Component.text("Cooldown: " + cooldownSeconds, NamedTextColor.YELLOW));
        lore.add(Component.empty());
        lore.addAll(TextUtility.wordWrap(getDescription()));
        lore.add(Component.empty());
        if (isUnlocked) {
            lore.add(Component.text("Left-click to add to", NamedTextColor.GREEN));
            lore.add(Component.text("hotbar", NamedTextColor.GREEN));
        } else {
            Skill prerequisite = getPrerequisite();
            if (prerequisite != null && !prerequisite.isUnlocked(pc)) {
                lore.add(Component.text("Requires " + prerequisite.getName(), NamedTextColor.RED));
            } else if (pc.getLevel() < getLevel()) {
                lore.add(Component.text("Requires level " + getLevel(), NamedTextColor.RED));
            } else {
                lore.add(Component.text("Shift-click to", NamedTextColor.GREEN));
                lore.add(Component.text("unlock", NamedTextColor.GREEN));
            }
        }
        return ItemStackUtility.createItemStack(icon, displayName, lore);
    }

    @ApiStatus.Internal
    public ItemStack getHotbarItemStack() {
        Material icon = getIcon();
        Component displayName = Component.text(getName(), NamedTextColor.YELLOW);
        List<TextComponent> lore = new ArrayList<>();
        lore.add(Component.text("Active Skill", NamedTextColor.GRAY));
        lore.add(Component.text("Mana Cost: " + manaCost));
        int cooldownSeconds = cooldown.toSecondsPart();
        lore.add(Component.text("Cooldown: " + cooldownSeconds, NamedTextColor.YELLOW));
        lore.add(Component.empty());
        lore.addAll(TextUtility.wordWrap(getDescription()));
        return ItemStackUtility.createItemStack(icon, displayName, lore);
    }
}
