package com.mcquest.server.playerclass;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.event.EventEmitter;
import com.mcquest.server.event.SkillUnlockEvent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.file.FileTree;

import java.io.InputStream;
import java.util.concurrent.Callable;

public abstract class Skill {
    static final Material SKILL_MATERIAL = Material.WOODEN_AXE;

    private final int id;
    private final String name;
    private final int level;
    private final @Nullable Integer prerequisiteId;
    private final Callable<InputStream> icon;
    private final String description;
    private final int skillTreeRow;
    private final int skillTreeColumn;
    private final EventEmitter<SkillUnlockEvent> onUnlock;
    PlayerClass playerClass;

    Skill(int id, String name, int level, @Nullable Integer prerequisiteId,
          Callable<InputStream> icon, String description, int skillTreeRow,
          int skillTreeColumn) {
        this.id = id;
        this.name = name;
        this.level = level;
        this.prerequisiteId = prerequisiteId;
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

    public Callable<InputStream> getIcon() {
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

    /**
     * Returns the number of textures written.
     */
    @ApiStatus.Internal
    public abstract int writeResources(FileTree tree, int customModelDataStart);
}
