package net.mcquest.core.login;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.persistence.PlayerCharacterData;
import net.mcquest.core.playerclass.PlayerClass;
import net.mcquest.core.util.ItemStackUtility;
import net.mcquest.core.zone.Zone;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;

import java.util.Collections;
import java.util.List;

class Buttons {
    static ItemStack selectCharacter(int slot, PlayerCharacterData data, Mmorpg mmorpg) {
        if (data == null) {
            return ItemStackUtility.create(
                    Material.EMERALD,
                    Component.text("Create character", NamedTextColor.GOLD),
                    Collections.emptyList()
            ).build();
        }

        int level = PlayerCharacter.levelForExperiencePoints(data.experiencePoints());
        PlayerClass playerClass = mmorpg.getPlayerClassManager()
                .getPlayerClass(data.playerClassId());
        Zone zone = mmorpg.getZoneManager().getZone(data.zoneId());

        return ItemStackUtility.create(
                Material.IRON_SWORD,
                Component.text(String.format("Character %d", slot + 1), NamedTextColor.GOLD),
                List.of(
                        Component.text(
                                String.format("Level %d %s", level, playerClass.getName()),
                                NamedTextColor.YELLOW
                        ),
                        Component.text(zone.getName(), zone.getType().getTextColor())
                )
        ).build();
    }

    static ItemStack selectClass(PlayerClass playerClass) {
        return ItemStackUtility.create(
                Material.IRON_SWORD,
                Component.text(playerClass.getName(), NamedTextColor.YELLOW),
                Collections.emptyList()
        ).build();
    }

    static ItemStack deleteCharactersMenu() {
        return ItemStackUtility.create(
                Material.BARRIER,
                Component.text("Delete character", NamedTextColor.RED),
                Collections.emptyList()
        ).build();
    }

    static ItemStack deleteCharacter(int characterSlot) {
        return ItemStackUtility.create(
                Material.IRON_SWORD,
                Component.text(
                        String.format("Delete character %d", characterSlot + 1),
                        NamedTextColor.RED
                ),
                Collections.emptyList()
        ).build();
    }

    static ItemStack goBack() {
        return ItemStackUtility.create(
                Material.BARRIER,
                Component.text("Go back", NamedTextColor.RED),
                Collections.emptyList()
        ).build();
    }
}
