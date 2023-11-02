package net.mcquest.core.persistence;

import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.instance.Instance;
import net.mcquest.core.item.PlayerCharacterInventory;
import net.mcquest.core.item.Weapon;
import net.mcquest.core.music.Song;
import net.mcquest.core.playerclass.PlayerClass;
import net.mcquest.core.util.JsonUtility;
import net.mcquest.core.zone.Zone;
import net.minestom.server.coordinate.Pos;

public record PlayerCharacterData(
        String playerClassId,
        String instanceId,
        Pos position,
        String respawnInstanceId,
        Pos respawnPosition,
        String zoneId,
        double health,
        double maxHealth,
        double mana,
        double maxMana,
        double healthRegenRate,
        double manaRegenRate,
        double experiencePoints,
        int skillPoints,
        int money,
        PersistentItem[] items,
        PersistentQuestObjectiveData[] questObjectiveData,
        String[] completedQuestIds,
        String[] trackedQuestIds,
        String songId,
        boolean canMount,
        String[] ownedMountIds
) {
    public static PlayerCharacterData create(PlayerClass playerClass, Instance instance,
                                             Pos position, Zone zone, Weapon weapon) {
        return new PlayerCharacterData(
                playerClass.getId(),
                instance.getId(),
                position,
                instance.getId(),
                position,
                zone.getId(),
                20,
                20,
                1,
                1,
                0,
                0,
                0,
                0,
                0,
                new PersistentItem[]{new PersistentItem(
                        weapon.getId(),
                        1,
                        PlayerCharacterInventory.WEAPON_SLOT
                )},
                new PersistentQuestObjectiveData[0],
                new String[0],
                new String[0],
                null,
                true,
                new String[0]
        );
    }

    public static PlayerCharacterData save(PlayerCharacter pc) {
        Song song = pc.getMusicPlayer().getSong();

        return new PlayerCharacterData(
                pc.getPlayerClass().getId(),
                pc.getInstance().getId(),
                pc.getPosition(),
                pc.getRespawnInstance().getId(),
                pc.getRespawnPosition(),
                pc.getZone().getId(),
                pc.getHealth(),
                pc.getMaxHealth(),
                pc.getMana(),
                pc.getMaxMana(),
                pc.getHealthRegenRate(),
                pc.getManaRegenRate(),
                pc.getExperiencePoints(),
                pc.getSkillManager().getSkillPoints(),
                pc.getMoney().getValue(),
                new PersistentItem[0], // TODO
                new PersistentQuestObjectiveData[0], // TODO
                new String[0], // TODO
                new String[0], // TODO
                song == null ? null : song.getId(),
                pc.canMount(),
                new String[0] // TODO
        );
    }

    public static PlayerCharacterData fromJson(String json) {
        return JsonUtility.fromJson(json, PlayerCharacterData.class);
    }

    public String toJson() {
        return JsonUtility.toJson(this);
    }
}
