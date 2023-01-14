package com.mcquest.server.event;

import com.mcquest.server.character.Character;
import com.mcquest.server.character.DamageSource;
import net.minestom.server.event.Event;

public class CharacterDamageEvent implements Event {
    private final Character character;
    private final DamageSource source;
    private final double amount;

    public CharacterDamageEvent(Character character, DamageSource source, double amount) {
        this.character = character;
        this.source = source;
        this.amount = amount;
    }

    public Character getCharacter() {
        return character;
    }

    public DamageSource getSource() {
        return source;
    }

    public double getAmount() {
        return amount;
    }
}
