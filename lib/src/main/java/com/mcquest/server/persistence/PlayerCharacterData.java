package com.mcquest.server.persistence;

import com.mcquest.server.character.PlayerCharacter;

public class PlayerCharacterData {
    private final String displayName;
    private final double health;
    private final double maxHealth;
    private final double mana;
    private final double maxMana;
    private final double experiencePoints;


    private PlayerCharacterData() {
        PlayerCharacter pc = null;
        this.displayName = pc.getDisplayName().toString(); // TODO: serialize
        this.health = pc.getHealth();
        this.maxHealth = pc.getMaxHealth();
        this.mana = pc.getMana();
        this.maxMana = pc.getMaxMana();
        this.experiencePoints = pc.getExperiencePoints();
    }

    public static PlayerCharacterData create() {
        return new PlayerCharacterData();
    }

    public static PlayerCharacterData save(PlayerCharacter pc) {
        return new PlayerCharacterData();
    }
}
