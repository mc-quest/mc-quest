package com.mcquest.server.persistence;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.instance.Instance;
import com.mcquest.server.item.Weapon;
import com.mcquest.server.music.Song;
import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.zone.Zone;
import net.minestom.server.coordinate.Pos;
import org.jetbrains.annotations.Nullable;

public class PlayerCharacterData {
    private int playerClassId;
    private int instanceId;
    private Pos position;
    private Pos respawnPosition;
    private int zoneId;
    private double health;
    private double maxHealth;
    private double mana;
    private double maxMana;
    private double experiencePoints;
    private PersistentItem[] items;
    private PersistentQuestObjectiveData[] questObjectiveData;
    private int[] completedQuestIds;
    private int[] trackedQuestIds;
    private Integer songId;
    private boolean canMount;

    private PlayerCharacterData() {
    }

    /**
     * Constructs a new PlayerCharacterData for a new PlayerCharacter.
     */
    public static PlayerCharacterData create(PlayerClass playerClass, Instance instance,
                                             Pos position, Zone zone, Weapon weapon) {
        PlayerCharacterData data = new PlayerCharacterData();
        data.playerClassId = playerClass.getId();
        data.instanceId = instance.getId();
        data.position = position;
        data.respawnPosition = position;
        data.zoneId = zone.getId();
        data.health = 1;
        data.maxHealth = 1;
        data.mana = 1;
        data.maxMana = 1;
        data.experiencePoints = 0;
        data.items = new PersistentItem[]{new PersistentItem(weapon.getId(), 1, 8)};
        data.questObjectiveData = new PersistentQuestObjectiveData[0];
        data.completedQuestIds = new int[0];
        data.trackedQuestIds = new int[0];
        data.songId = null;
        data.canMount = true;
        return data;
    }

    public static PlayerCharacterData save(PlayerCharacter pc) {
        PlayerCharacterData data = new PlayerCharacterData();
        data.playerClassId = pc.getPlayerClass().getId();
        data.instanceId = ((Instance) pc.getInstance()).getId();
        data.position = pc.getPosition();
        data.health = pc.getHealth();
        data.maxHealth = pc.getMaxHealth();
        data.mana = pc.getMana();
        data.maxMana = pc.getMaxMana();
        data.experiencePoints = pc.getExperiencePoints();
        Song song = pc.getMusicPlayer().getSong();
        data.songId = song == null ? null : song.getId();
        data.canMount = pc.canMount();
        return data;
    }

    public int getPlayerClassId() {
        return playerClassId;
    }

    public int getInstanceId() {
        return instanceId;
    }

    public Pos getPosition() {
        return position;
    }

    public Pos getRespawnPosition() {
        return respawnPosition;
    }

    public int getZoneId() {
        return zoneId;
    }

    public double getHealth() {
        return health;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getMana() {
        return mana;
    }

    public double getMaxMana() {
        return maxMana;
    }

    public double getExperiencePoints() {
        return experiencePoints;
    }

    public PersistentItem[] getItems() {
        return items;
    }

    public PersistentQuestObjectiveData[] getQuestObjectiveData() {
        return questObjectiveData;
    }

    public int[] getCompletedQuestIds() {
        return completedQuestIds;
    }

    public int[] getTrackedQuestIds() {
        return trackedQuestIds;
    }

    public @Nullable Integer getSongId() {
        return songId;
    }

    public boolean canMount() {
        return canMount;
    }
}
