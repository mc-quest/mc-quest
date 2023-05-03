package com.mcquest.server.playerclass;

import com.mcquest.server.asset.Asset;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.text.WordWrap;
import com.mcquest.server.util.ItemStackUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.file.FileTree;

import java.util.ArrayList;
import java.util.List;

public class PassiveSkill extends Skill {
    PassiveSkill(int id, String name, int level, @Nullable Integer prerequisiteId,
                 Asset icon, String description, int skillTreeRow, int skillTreeColumn) {
        super(id, name, level, prerequisiteId, icon, description, skillTreeRow, skillTreeColumn);
    }

    @Override
    ItemStack getSkillTreeItemStack(PlayerCharacter pc) {
        boolean isUnlocked = isUnlocked(pc);
        Material icon = isUnlocked ? null /* TODO */ : Material.BARRIER;
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
        return ItemStackUtility.createItemStack(icon, displayName, lore);
    }

    @Override
    @ApiStatus.Internal
    public int writeResources(FileTree tree, int customModelDataStart) {
        // TODO
        return 2;
    }
}
