package net.mcquest.core.item;

import com.google.common.collect.ListMultimap;
import net.mcquest.core.asset.Asset;
import net.mcquest.core.asset.AssetTypes;
import net.mcquest.core.quest.QuestObjective;
import net.mcquest.core.resourcepack.Materials;
import net.mcquest.core.resourcepack.ResourcePackUtility;
import net.mcquest.core.util.ItemStackUtility;
import net.kyori.adventure.key.Key;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.ItemOverride;

import java.util.Collections;

public class QuestItem extends Item {
    private final Asset icon;
    private QuestObjective objective;
    private int customModelData;

    private QuestItem(Builder builder) {
        super(builder);
        icon = builder.icon;
    }

    public QuestObjective getObjective() {
        return objective;
    }

    public void registerObjective(QuestObjective objective) {
        if (this.objective != null) {
            throw new IllegalStateException();
        }

        this.objective = objective;
    }

    @Override
    public int getStackSize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ItemStack getItemStack() {
        return ItemStackUtility
                .create(Materials.ITEM_DEFAULT, getDisplayName(), Collections.emptyList())
                .set(ID_TAG, getId())
                .meta(builder -> builder.customModelData(customModelData))
                .build();
    }

    @Override
    @ApiStatus.Internal
    public void writeResources(FileTree tree, ListMultimap<Material, ItemOverride> overrides) {
        Key key = ItemUtility.resourcePackKey(this);
        customModelData = ResourcePackUtility
                .writeIcon(tree, icon, key, Materials.ITEM_DEFAULT, overrides);
    }


    public static IdStep builder() {
        return new Builder();
    }

    public interface IdStep {
        NameStep id(int id);
    }

    public interface NameStep {
        QualityStep name(String name);
    }

    public interface QualityStep {
        IconStep quality(ItemQuality quality);
    }

    public interface IconStep {
        BuildStep icon(Asset model);
    }

    public interface BuildStep {
        BuildStep description(String description);

        QuestItem build();
    }

    private static class Builder extends Item.Builder
            implements IdStep, NameStep, QualityStep, IconStep, BuildStep {
        private Asset icon;

        @Override
        public NameStep id(int id) {
            this.id = id;
            return this;
        }

        @Override
        public QualityStep name(String name) {
            this.name = name;
            return this;
        }

        @Override
        public IconStep quality(ItemQuality quality) {
            this.quality = quality;
            return this;
        }

        @Override
        public BuildStep icon(Asset icon) {
            icon.requireType(AssetTypes.PNG);
            this.icon = icon;
            return this;
        }

        @Override
        public BuildStep description(String description) {
            this.description = description;
            return this;
        }

        @Override
        public QuestItem build() {
            return new QuestItem(this);
        }
    }
}
