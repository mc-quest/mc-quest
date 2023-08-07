package com.mcquest.core.character;

import com.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.hologram.Hologram;

import java.util.EnumMap;
import java.util.Map;

class Nameplate {
    private final Character character;
    private final Map<Attitude, Hologram> names;
    private Hologram healthBar;

    Nameplate(Character character) {
        this.character = character;
        names = new EnumMap<>(Attitude.class);
    }

    void spawn() {
        Instance instance = character.getInstance();
        Pos namePosition = namePosition();

        for (Attitude attitude : Attitude.values()) {
            Hologram name = new Hologram(instance, namePosition, character.nameText(attitude));
            name.getEntity().setAutoViewable(false);
            names.put(attitude, name);
        }

        healthBar = new Hologram(instance, healthBarPosition(), character.healthBarText());
        healthBar.getEntity().setAutoViewable(false);
    }

    void despawn() {
        for (Hologram name : names.values()) {
            name.remove();
        }
        healthBar.remove();

        names.clear();
        healthBar = null;
    }

    void updateViewer(PlayerCharacter pc) {
        if (pc == character) {
            return;
        }

        Attitude attitude = character.getAttitude(pc);
        Player player = pc.getPlayer();
        names.forEach((att, name) -> {
            if (att == attitude) {
                name.addViewer(player);
            } else {
                name.removeViewer(player);
            }
        });

        healthBar.addViewer(player);
    }

    void updateInstance() {
        despawn();
        spawn();
    }

    void updatePosition() {
        for (Hologram name : names.values()) {
            name.setPosition(namePosition());
        }

        healthBar.setPosition(healthBarPosition());
    }

    void updateNameText() {
        names.forEach((attitude, name) -> {
            name.setText(character.nameText(attitude));
        });
    }

    void updateHealthBarText() {
        healthBar.setText(character.healthBarText());
    }

    private Pos namePosition() {
        return character.getPosition().withY(y -> y + character.getHeight());
    }

    private Pos healthBarPosition() {
        return character.getPosition().withY(y -> y + character.getHeight() - 0.25);
    }
}
