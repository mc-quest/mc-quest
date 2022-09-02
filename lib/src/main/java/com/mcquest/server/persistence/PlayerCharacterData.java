package com.mcquest.server.persistence;

import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.item.Weapon;
import com.mcquest.server.playerclass.PlayerClass;
import com.mcquest.server.playerclass.PlayerClassManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

public class PlayerCharacterData {
    private String playerClass;
    private double health;
    private double maxHealth;
    private double mana;
    private double maxMana;
    private double experiencePoints;

    private PlayerCharacterData() {
    }

    /**
     * Constructs a new PlayerCharacterData for a new PlayerCharacter.
     */
    public static PlayerCharacterData create(PlayerClass playerClass, Instance instance, Pos position, Weapon weapon) {
        PlayerCharacterData data = new PlayerCharacterData();
        data.playerClass = playerClass.getName();
        data.health = 1;
        data.maxHealth = 1;
        data.mana = 1;
        data.maxMana = 1;
        data.experiencePoints = 0;
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
        return data;
    }

    public PlayerClass getPlayerClass() {
        return PlayerClassManager.getPlayerClass(playerClass);
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
}
