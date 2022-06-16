package com.mcquest.server.npc;

import com.mcquest.server.character.CharacterHitbox;
import com.mcquest.server.character.NonPlayerCharacter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

public class Bear extends NonPlayerCharacter {
    private static final Component DISPLAY_NAME =
            Component.text("Bear", NamedTextColor.RED);

    private final CharacterHitbox hitbox;

    public Bear(Instance instance, Pos position) {
        super(DISPLAY_NAME, 5, instance, position);
        hitbox = null;
    }

    @Override
    public void spawn() {
        super.spawn();
    }

    @Override
    public void despawn() {
        super.despawn();
    }
}
