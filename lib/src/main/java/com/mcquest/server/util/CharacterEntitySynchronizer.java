package com.mcquest.server.util;

import com.mcquest.server.character.Character;
import net.minestom.server.entity.Entity;

public class CharacterEntitySynchronizer {
    private Character character;
    private Mode mode;
    private Entity entity;

    public CharacterEntitySynchronizer(Character character, Mode mode) {
        this.character = character;
        this.mode = mode;
        entity = null;
    }

    public Character getCharacter() {
        return character;
    }

    public void setCharacter(Character character) {
        this.character = character;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Mode getMode() {
        return mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public boolean isEnabled() {
        // TODO
        return false;
    }

    public void setEnabled() {
        // TODO
    }

    public static enum Mode {
        CHARACTER_FOLLOWS_ENTITY, ENTITY_FOLLOWS_CHARACTER
    }
}
