package com.mcquest.core.playerclass;

import com.mcquest.core.asset.Asset;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.event.EventEmitter;
import com.mcquest.core.event.SkillUnlockEvent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.ItemOverride;

import java.util.List;

public abstract class Skill {
    private final int id;
    private final String name;
    private final int level;
    private final @Nullable Integer prerequisiteId;
    private final Asset icon;
    private final String description;
    private final int skillTreeRow;
    private final int skillTreeColumn;
    private final EventEmitter<SkillUnlockEvent> onUnlock;
    PlayerClass playerClass;

    Skill(int id, String name, int level, @Nullable Integer prerequisiteId,
          Asset icon, String description, int skillTreeRow, int skillTreeColumn) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.prerequisiteId = prerequisiteId;
        icon.requireType("png");
        this.icon = icon;
        this.description = description;
        this.skillTreeRow = skillTreeRow;
        this.skillTreeColumn = skillTreeColumn;
        this.onUnlock = new EventEmitter<>();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public @Nullable Skill getPrerequisite() {
        if (prerequisiteId == null) {
            return null;
        }
        return playerClass.getSkill(prerequisiteId);
    }

    public Asset getIcon() {
        return icon;
    }

    public String getDescription() {
        return description;
    }

    public int getSkillTreeRow() {
        return skillTreeRow;
    }

    public int getSkillTreeColumn() {
        return skillTreeColumn;
    }

    public EventEmitter<SkillUnlockEvent> onUnlock() {
        return onUnlock;
    }

    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    public boolean isUnlocked(PlayerCharacter pc) {
        return pc.getSkillManager().isUnlocked(this);
    }

    abstract ItemStack getSkillTreeItemStack(PlayerCharacter pc);

    @ApiStatus.Internal
    public abstract void writeResources(FileTree tree, List<ItemOverride> overrides);
}
