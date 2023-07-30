package com.mcquest.core.character;

import com.mcquest.core.instance.Instance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
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
            Hologram name = new Hologram(instance, namePosition, nameText(attitude));
            name.getEntity().setAutoViewable(false);
            names.put(attitude, name);
        }

        healthBar = new Hologram(instance, healthBarPosition(), healthBarText());
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
        Attitude attitude = character.getAttitude(pc);
        Player player = pc.getPlayer();
        names.forEach((att, name) -> {
            if (att == attitude) {
                name.addViewer(player);
            } else {
                name.removeViewer(player);
            }
        });
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
            name.setText(nameText(attitude));
        });
    }

    void updateHealthBarText() {
        healthBar.setText(healthBarText());
    }

    private Pos namePosition() {
        return character.getPosition().withY(y -> y + character.getHeight());
    }

    private Pos healthBarPosition() {
        return character.getPosition().withY(y -> y + character.getHeight() - 0.25);
    }

    private TextComponent nameText(Attitude attitude) {
        return Component.text("[", NamedTextColor.GRAY)
                .append(Component.text("Lv. " + character.getLevel(), NamedTextColor.GOLD))
                .append(Component.text("] ", NamedTextColor.GRAY))
                .append(Component.text(character.getName(), attitude.getColor()));
    }

    private TextComponent healthBarText() {
        int numBars = 20;
        double ratio = character.getHealth() / character.getMaxHealth();
        int numRedBars = (int) Math.ceil(numBars * ratio);
        int numGrayBars = numBars - numRedBars;
        return Component.text("[", NamedTextColor.GRAY)
                .append(Component.text("|".repeat(numRedBars), NamedTextColor.RED))
                .append(Component.text("|".repeat(numGrayBars), NamedTextColor.GRAY))
                .append(Component.text("]", NamedTextColor.GRAY));
    }
}
