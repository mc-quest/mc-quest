package net.mcquest.core.playerclass;

import net.mcquest.core.asset.Asset;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.EventEmitter;
import net.mcquest.core.event.SkillUnlockEvent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;
import team.unnamed.creative.file.FileTree;
import team.unnamed.creative.model.ItemOverride;

import java.util.List;

public abstract class Skill {
    private final int id;
    private final String name;
    private final int level;
    private final Integer prerequisiteId;
    private final Asset icon;
    private final String description;
    private final int skillTreeRow;
    private final int skillTreeColumn;
    private final EventEmitter<SkillUnlockEvent> onUnlock;
    PlayerClass playerClass;

    Skill(Builder builder) {
        id = builder.id;
        name = builder.name;
        level = builder.level;
        prerequisiteId = builder.prerequisiteId;
        icon = builder.icon;
        description = builder.description;
        skillTreeRow = builder.skillTreeRow;
        skillTreeColumn = builder.skillTreeColumn;
        onUnlock = new EventEmitter<>();
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

    public Skill getPrerequisite() {
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

    int getSkillTreeSlot() {
        return 9 * skillTreeRow + skillTreeColumn;
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

    static class Builder {
        int id;
        String name;
        int level;
        Asset icon;
        String description;
        int skillTreeRow;
        int skillTreeColumn;
        int prerequisiteId;
    }
}
