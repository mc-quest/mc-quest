package com.mcquest.server.npc;

import com.mcquest.server.Mmorpg;
import com.mcquest.server.character.NonPlayerCharacter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.instance.Instance;

public class TrainingDummy extends NonPlayerCharacter {
    private static final Component DISPLAY_NAME = Component.text("Training Dummy", NamedTextColor.RED);

    public TrainingDummy(Mmorpg mmorpg, Instance instance, Pos position) {
        super(DISPLAY_NAME, 1, instance, position);
    }
}
