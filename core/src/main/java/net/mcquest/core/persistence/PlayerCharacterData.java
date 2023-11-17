package net.mcquest.core.persistence;

import net.mcquest.core.cartography.Map;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.event.PlayerCharacterCreateEvent;
import net.mcquest.core.music.Song;
import net.mcquest.core.playerclass.PlayerClass;
import net.mcquest.core.util.JsonUtility;
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
        PersistentInventory inventory,
        PersistentQuestObjectiveData[] questObjectiveData,
        String[] completedQuestIds,
        String[] trackedQuestIds,
        String mapId,
        String songId,
        boolean canMount,
        String[] ownedMountIds
) {
    public static PlayerCharacterData create(
            PlayerClass playerClass,
            PlayerCharacterCreateEvent.Result characterCreateResult
    ) {
        return new PlayerCharacterData(
                playerClass.getId(),
                characterCreateResult.instance().getId(),
                characterCreateResult.position(),
                characterCreateResult.respawnInstance().getId(),
                characterCreateResult.respawnPosition(),
                characterCreateResult.zone().getId(),
                characterCreateResult.maxHealth(),
                characterCreateResult.maxHealth(),
                characterCreateResult.maxMana(),
                characterCreateResult.maxMana(),
                characterCreateResult.healthRegenRate(),
                characterCreateResult.manaRegenRate(),
                0,
                1,
                0,
                new PersistentInventory(
                        characterCreateResult.weapon().getId(),
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        new PersistentItem[3 * 9]
                ),
                new PersistentQuestObjectiveData[0],
                new String[0],
                new String[0],
                characterCreateResult.map().getId(),
                characterCreateResult.song().getId(),
                true,
                new String[0]
        );
    }

    public static PlayerCharacterData save(PlayerCharacter pc) {
        Song song = pc.getMusicPlayer().getSong();
        Map map = pc.getMapViewer().getMap();

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
                pc.getInventory().save(),
                new PersistentQuestObjectiveData[0], // TODO
                new String[0], // TODO
                new String[0], // TODO
                map == null ? null : map.getId(),
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
