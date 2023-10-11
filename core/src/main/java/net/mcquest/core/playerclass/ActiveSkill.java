package net.mcquest.core.playerclass;

import net.mcquest.core.asset.Asset;
import net.mcquest.core.asset.AssetTypes;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.ActiveSkillUseEvent;
import net.mcquest.core.event.EventEmitter;
import net.mcquest.core.resourcepack.Materials;
import net.mcquest.core.resourcepack.Namespaces;
import net.mcquest.core.resourcepack.ResourcePackUtility;
import net.mcquest.core.text.WordWrap;
import net.mcquest.core.ui.Hotbar;
import net.mcquest.core.util.ItemStackUtility;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
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

    private ActiveSkill(Builder builder) {
        super(builder);
        this.manaCost = builder.manaCost;
        this.cooldown = builder.cooldown;
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
                : customModelDataStart + 2 * Hotbar.COOLDOWN_TEXTURES + 1;

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

        // Unusable cooldown textures.
        for (int i = 1; i <= Hotbar.COOLDOWN_TEXTURES; i++) {
            ResourcePackUtility.writeCooldownIconUnusable(
                    tree,
                    getIcon(),
                    Key.key(Namespaces.SKILLS, String.format("%d-%d-%d-unusable", playerClass.getId(), getId(), i)),
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


    public interface IdStep {
        NameStep id(int id);
    }

    public interface NameStep {
        LevelStep name(String name);
    }

    public interface LevelStep {
        IconStep level(int level);
    }

    public interface IconStep {
        DescriptionStep icon(Asset icon);
    }

    public interface DescriptionStep {
        SkillTreePositionStep description(String description);
    }

    public interface SkillTreePositionStep {
        ManaCostStep skillTreePosition(int row, int column);
    }

    public interface ManaCostStep {
        CooldownStep manaCost(double manaCost);
    }

    public interface CooldownStep {
        BuildStep cooldown(Duration cooldown);
    }

    public interface BuildStep {
        BuildStep prerequisite(int id);

        PlayerClass.Builder build();
    }

    static class Builder extends Skill.Builder implements IdStep, NameStep,
            LevelStep, IconStep, DescriptionStep, SkillTreePositionStep,
            ManaCostStep, CooldownStep, BuildStep {
        private final PlayerClass.Builder playerClassBuilder;
        private double manaCost;
        private Duration cooldown;

        Builder(PlayerClass.Builder playerClassBuilder) {
            this.playerClassBuilder = playerClassBuilder;
        }

        @Override
        public NameStep id(int id) {
            this.id = id;
            return this;
        }

        @Override
        public LevelStep name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public IconStep level(int level) {
            this.level = level;
            return this;
        }

        @Override
        public DescriptionStep icon(Asset icon) {
            icon.requireType(AssetTypes.PNG);
            this.icon = icon;
            return this;
        }

        @Override
        public SkillTreePositionStep description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public ManaCostStep skillTreePosition(int row, int column) {
            this.skillTreeRow = row;
            this.skillTreeColumn = column;
            return this;
        }

        @Override
        public CooldownStep manaCost(double manaCost) {
            this.manaCost = manaCost;
            return this;
        }

        @Override
        public BuildStep cooldown(Duration cooldown) {
            this.cooldown = cooldown;
            return this;
        }

        @Override
        public BuildStep prerequisite(int id) {
            this.prerequisiteId = id;
            return this;
        }

        @Override
        public PlayerClass.Builder build() {
            ActiveSkill skill = new ActiveSkill(this);
            playerClassBuilder.skills.add(skill);
            return playerClassBuilder;
        }
    }
}
