package net.mcquest.core.playerclass;

import net.mcquest.core.asset.Asset;
import net.mcquest.core.asset.AssetTypes;
import net.mcquest.core.character.PlayerCharacter;
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
import net.minestom.server.item.Material;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.ItemOverride;

import java.util.ArrayList;
import java.util.List;

public class PassiveSkill extends Skill {
    private int customModelDataStart;

    PassiveSkill(Builder builder) {
        super(builder);
    }

    @Override
    ItemStack getSkillTreeItemStack(PlayerCharacter pc) {
        boolean isUnlocked = isUnlocked(pc);

        Component displayName = Component.text(getName(), NamedTextColor.YELLOW);

        List<TextComponent> lore = new ArrayList<>();

        lore.add(Component.text("Passive Skill", NamedTextColor.GRAY));

        lore.add(Component.empty());
        lore.addAll(WordWrap.wrap(getDescription()));

        if (!isUnlocked) {
            lore.add(Component.empty());

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
                : customModelDataStart + 1;

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
                Key.key(Namespaces.SKILLS, String.format("%s-%s", playerClass.getId(), getId())),
                overrides
        );

        // Locked texture.
        ResourcePackUtility.writeLockedIcon(
                tree,
                getIcon(),
                Key.key(Namespaces.SKILLS, String.format("%s-%s-locked", playerClass.getId(), getId())),
                overrides
        );
    }

    public interface IdStep {
        NameStep id(String id);
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
        BuildStep skillTreePosition(int row, int column);
    }

    public interface BuildStep {
        BuildStep prerequisite(String id);

        PlayerClass.Builder build();
    }

    static class Builder extends Skill.Builder implements IdStep, NameStep,
            LevelStep, IconStep, DescriptionStep, SkillTreePositionStep, BuildStep {
        private final PlayerClass.Builder playerClassBuilder;

        Builder(PlayerClass.Builder playerClassBuilder) {
            this.playerClassBuilder = playerClassBuilder;
        }

        @Override
        public NameStep id(String id) {
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
        public BuildStep skillTreePosition(int row, int column) {
            this.skillTreeRow = row;
            this.skillTreeColumn = column;
            return this;
        }

        @Override
        public BuildStep prerequisite(String id) {
            this.prerequisiteId = id;
            return this;
        }

        @Override
        public PlayerClass.Builder build() {
            PassiveSkill skill = new PassiveSkill(this);
            playerClassBuilder.skills.add(skill);
            return playerClassBuilder;
        }
    }
}
