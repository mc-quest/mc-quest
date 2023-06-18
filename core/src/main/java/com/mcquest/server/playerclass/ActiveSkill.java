package com.mcquest.server.playerclass;

import com.mcquest.server.asset.Asset;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.event.ActiveSkillUseEvent;
import com.mcquest.server.event.EventEmitter;
import com.mcquest.server.resourcepack.Materials;
import com.mcquest.server.resourcepack.Namespaces;
import com.mcquest.server.resourcepack.ResourcePackUtility;
import com.mcquest.server.text.WordWrap;
import com.mcquest.server.ui.Hotbar;
import com.mcquest.server.util.ItemStackUtility;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.ItemOverride;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ActiveSkill extends Skill {
    private static final int COOLDOWN_DIVISIONS = 16;

    private final double manaCost;
    private final Duration cooldown;
    private final EventEmitter<ActiveSkillUseEvent> onUse;
    int customModelDataStart;

    ActiveSkill(int id, String name, int level, @Nullable Integer prerequisiteId,
                Asset icon, String description, int skillTreeRow,
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

    public EventEmitter<ActiveSkillUseEvent> onUse() {
        return onUse;
    }

    public Duration getCooldown(PlayerCharacter pc) {
        return pc.getSkillManager().getCooldown(this);
    }

    @Override
    ItemStack getSkillTreeItemStack(PlayerCharacter pc) {
        boolean isUnlocked = isUnlocked(pc);
        Material icon = isUnlocked ? Materials.SKILL : Material.BARRIER;
        Component displayName = Component.text(getName(), NamedTextColor.YELLOW);
        List<TextComponent> lore = new ArrayList<>();
        lore.add(Component.text("Active Skill", NamedTextColor.GRAY));
        lore.add(Component.text("Mana Cost: " + (int) Math.round(manaCost), NamedTextColor.AQUA));
        int cooldownSeconds = cooldown.toSecondsPart();
        lore.add(Component.text("Cooldown: " + cooldownSeconds, NamedTextColor.GREEN));
        lore.add(Component.empty());
        lore.addAll(WordWrap.wrap(getDescription()));
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

    ItemStack getHotbarItemStack(PlayerCharacter pc) {
        int cooldownTexture = cooldownTexture(pc);
        return getHotbarItemStack(cooldownTexture);
    }

    private int cooldownTexture(PlayerCharacter pc) {
        return (int) Math.ceil(
                getCooldown(pc).toMillis() / getCooldown().toMillis());
    }

    private ItemStack getHotbarItemStack(int cooldownTexture) {
        int customModelData = customModelDataStart + cooldownTexture;
        Component displayName = Component.text(getName(), NamedTextColor.YELLOW);
        List<TextComponent> lore = new ArrayList<>();
        lore.add(Component.text("Active Skill", NamedTextColor.GRAY));
        lore.add(Component.text("Mana Cost: " + (int) Math.round(manaCost), NamedTextColor.AQUA));
        int cooldownSeconds = cooldown.toSecondsPart();
        lore.add(Component.text("Cooldown: " + cooldownSeconds, NamedTextColor.GREEN));
        lore.add(Component.empty());
        lore.addAll(WordWrap.wrap(getDescription()));
        return ItemStackUtility.createItemStack(Materials.SKILL, displayName, lore)
                .withTag(PlayerClassManager.PLAYER_CLASS_ID_TAG, playerClass.getId())
                .withTag(PlayerClassManager.SKILL_ID_TAG, getId())
                .withMeta(builder -> builder.customModelData(customModelData));
    }

    private ItemStack getInsufficientManaHotbarItemStack() {
        return getHotbarItemStack(Hotbar.COOLDOWN_TEXTURES);
    }

    @Override
    @ApiStatus.Internal
    public void writeResources(FileTree tree, List<ItemOverride> overrides) {
        // Default texture.
        customModelDataStart = ResourcePackUtility.writeIcon(
                tree,
                getIcon(),
                Key.key(Namespaces.SKILLS, String.format("%d-%d", playerClass.getId(), getId())),
                overrides
        );

        // Locked texture.


        // Cooldown textures.
        for (int i = 1; i <= COOLDOWN_DIVISIONS; i++) {

        }
    }
}
