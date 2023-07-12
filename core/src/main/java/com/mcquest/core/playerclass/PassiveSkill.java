package com.mcquest.core.playerclass;

import com.mcquest.core.asset.Asset;
import com.mcquest.core.asset.AssetTypes;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.resourcepack.Materials;
import com.mcquest.core.resourcepack.Namespaces;
import com.mcquest.core.resourcepack.ResourcePackUtility;
import com.mcquest.core.text.WordWrap;
import com.mcquest.core.util.ItemStackUtility;
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
        Material icon = isUnlocked ? Materials.SKILL : Material.BARRIER;
        Component displayName = Component.text(getName(), NamedTextColor.YELLOW);
        List<TextComponent> lore = new ArrayList<>();
        lore.add(Component.text("Passive Skill", NamedTextColor.GRAY));
        lore.add(Component.empty());
        lore.addAll(WordWrap.wrap(getDescription()));
        if (!isUnlocked) {
            lore.add(Component.empty());
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

        return ItemStackUtility.create(icon, displayName, lore)
                .meta(builder -> builder.customModelData(customModelDataStart))
                .build();
    }

    @Override
    @ApiStatus.Internal
    public void writeResources(FileTree tree, List<ItemOverride> overrides) {
        customModelDataStart = ResourcePackUtility.writeIcon(
                tree,
                getIcon(),
                Key.key(Namespaces.SKILLS, String.format("%d-%d", playerClass.getId(), getId())),
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
        BuildStep skillTreePosition(int row, int column);
    }

    public interface BuildStep {
        BuildStep prerequisite(int id);

        PlayerClass.Builder build();
    }

    static class Builder extends Skill.Builder implements IdStep, NameStep,
            LevelStep, IconStep, DescriptionStep, SkillTreePositionStep, BuildStep {
        private final PlayerClass.Builder playerClassBuilder;

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
        public BuildStep skillTreePosition(int row, int column) {
            this.skillTreeRow = row;
            this.skillTreeColumn = column;
            return this;
        }

        @Override
        public BuildStep prerequisite(int id) {
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
