package net.mcquest.core.character;

import net.mcquest.core.instance.Instance;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.hologram.Hologram;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
            remove(name);
        }
        names.clear();

        remove(healthBar);
        healthBar = null;
    }

    void updateViewer(PlayerCharacter pc) {
        if (pc == character) {
            return;
        }

        Attitude attitude = character.getAttitude(pc);
        Player player = pc.getEntity();
        names.forEach((att, name) -> {
            if (att == attitude && !character.isInvisible()) {
                name.addViewer(player);
            } else {
                name.removeViewer(player);
            }
        });

        if (character.isInvisible()) {
            healthBar.removeViewer(player);
        } else {
            healthBar.addViewer(player);
        }
    }

    void updateInstance() {
        despawn();
        spawn();
    }

    void updatePosition() {
        for (Hologram name : names.values()) {
            setPosition(name, namePosition());
        }

        if (healthBar != null) {
            setPosition(healthBar, healthBarPosition());
        }
    }

    void updateNameText() {
        names.forEach((attitude, name) -> {
            name.setText(character.nameText(attitude));
        });
    }

    void updateHealthBarText() {
        if (healthBar != null) {
            healthBar.setText(character.healthBarText());
        }
    }

    private Pos namePosition() {
        return character.getPosition().withY(y -> y + height() + 0.25);
    }

    private Pos healthBarPosition() {
        return character.getPosition().withY(y -> y + height());
    }

    private double height() {
        return character.getHitbox().getExtents().y();
    }

    private void setPosition(Hologram hologram, Pos position) {
        // Workaround for Minestom bug.
        if (hologram.getEntity().getChunk() == null) {
            return;
        }

        hologram.setPosition(position);
    }

    private void remove(Hologram hologram) {
        // Workaround for Minestom bug.
        Set<Player> viewers = new HashSet<>(hologram.getViewers());
        viewers.forEach(hologram::removeViewer);

        hologram.remove();
    }
}
