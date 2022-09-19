package com.mcquest.server.persistence;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.item.Weapon;
import com.mcquest.server.music.Song;
import com.mcquest.server.playerclass.PlayerClass;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;
import org.jetbrains.annotations.Nullable;

public class PlayerCharacterData {
    private String playerClass;
    private String instance;
    private Pos position;
    private Pos respawnPosition;
    private double health;
    private double maxHealth;
    private double mana;
    private double maxMana;
    private double experiencePoints;
    private PersistentItem[] items;
    private Integer songId;

    private PlayerCharacterData() {
    }

    /**
     * Constructs a new PlayerCharacterData for a new PlayerCharacter.
     */
    public static PlayerCharacterData create(Mmorpg mmorpg, PlayerClass playerClass,
                                             Instance instance, Pos position, Weapon weapon) {
        PlayerCharacterData data = new PlayerCharacterData();
        data.playerClass = playerClass.getName();
        data.instance = mmorpg.getInstanceManager().nameOf(instance);
        data.position = position;
        data.respawnPosition = position;
        data.health = 1;
        data.maxHealth = 1;
        data.mana = 1;
        data.maxMana = 1;
        data.experiencePoints = 0;
        data.items = new PersistentItem[46];
        data.items[4] = new PersistentItem(weapon.getId(), 1);
        data.songId = null;
        return data;
    }

    public static PlayerCharacterData save(PlayerCharacter pc) {
        PlayerCharacterData data = new PlayerCharacterData();
        data.playerClass = pc.getPlayerClass().getName();
        data.health = pc.getHealth();
        data.maxHealth = pc.getMaxHealth();
        data.mana = pc.getMana();
        data.maxMana = pc.getMaxMana();
        data.experiencePoints = pc.getExperiencePoints();
        Song song = pc.getMusicPlayer().getSong();
        data.songId = song == null ? null : song.getId();
        return data;
    }

    public String getPlayerClass() {
        return playerClass;
    }

    public String getInstance() {
        return instance;
    }

    public Pos getPosition() {
        return position;
    }

    public Pos getRespawnPosition() {
        return respawnPosition;
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

    public @Nullable Integer getSongId() {
        return songId;
    }
}
