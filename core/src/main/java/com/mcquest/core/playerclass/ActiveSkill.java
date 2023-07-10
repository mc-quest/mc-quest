package com.mcquest.core.playerclass;

import com.mcquest.core.asset.Asset;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.event.ActiveSkillUseEvent;
import com.mcquest.core.event.EventEmitter;
import com.mcquest.core.resourcepack.Materials;
import com.mcquest.core.resourcepack.Namespaces;
import com.mcquest.core.resourcepack.ResourcePackUtility;
import com.mcquest.core.text.WordWrap;
import com.mcquest.core.ui.Hotbar;
import com.mcquest.core.util.ItemStackUtility;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.ItemOverride;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActiveSkill extends Skill {
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
            boolean unlockable = true;

            if (pc.getLevel() < getLevel()) {
                unlockable = false;
                lore.add(Component.text("Requires level " + getLevel(), NamedTextColor.RED));
            }

            Skill prerequisite = getPrerequisite();
            if (prerequisite != null && !prerequisite.isUnlocked(pc)) {
                unlockable = false;
                lore.add(Component.text("Requires " + prerequisite.getName(), NamedTextColor.RED));
            }

            if (unlockable) {
                lore.add(Component.text("Shift-click to", NamedTextColor.GREEN));
                lore.add(Component.text("unlock", NamedTextColor.GREEN));
            }
        }

        int customModelData = isUnlocked
                ? customModelDataStart
                : customModelDataStart + Hotbar.COOLDOWN_TEXTURES + 1;

        return ItemStackUtility.create(Materials.SKILL, displayName, lore)
                .set(SkillManager.SKILL_ID_TAG, getId())
                .meta(builder -> builder.customModelData(customModelData))
                .build();
    }

    ItemStack getCursorItemStack() {
        Component displayName = Component.text(getName(), NamedTextColor.YELLOW);
        return ItemStackUtility.create(Materials.SKILL, displayName, Collections.emptyList())
                .set(SkillManager.SKILL_ID_TAG, getId())
                .meta(builder -> builder.customModelData(customModelDataStart))
                .build();
    }

    ItemStack getHotbarItemStack(PlayerCharacter pc) {
        int cooldownTexture = cooldownTexture(pc);
        return getHotbarItemStack(cooldownTexture)
                .withAmount(getCooldown(pc).toSecondsPart() + 1);
    }

    private int cooldownTexture(PlayerCharacter pc) {
        return (int) Math.ceil(Hotbar.COOLDOWN_TEXTURES *
                (double) getCooldown(pc).toMillis() / getCooldown().toMillis());
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
        return ItemStackUtility.create(Materials.SKILL, displayName, lore)
                .set(SkillManager.SKILL_ID_TAG, getId())
                .meta(builder -> builder.customModelData(customModelData))
                .build();
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

        // Cooldown textures.
        for (int i = 1; i <= Hotbar.COOLDOWN_TEXTURES; i++) {
            ResourcePackUtility.writeCooldownIcon(
                    tree,
                    getIcon(),
                    Key.key(Namespaces.SKILLS, String.format("%d-%d-%d", playerClass.getId(), getId(), i)),
                    i,
                    overrides
            );
        }

        // Locked texture.
        ResourcePackUtility.writeLockedIcon(
                tree,
                getIcon(),
                Key.key(Namespaces.SKILLS, String.format("%d-%d-locked", playerClass.getId(), getId())),
                overrides
        );
    }
}
