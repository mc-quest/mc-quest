package com.mcquest.server.npc;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.instance.Instance;
import com.mcquest.server.constants.Skins;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.sound.SoundEvent;

public class GuardThomas extends StaticHuman {
    public GuardThomas(Mmorpg mmorpg, Instance instance, Pos position) {
        super(mmorpg, instance, position, Skins.GUARD_MALE);
        setName("Guard Thomas");
        setLevel(10);
    }

    @Override
    protected void onInteract(PlayerCharacter pc) {
        speak(pc, Component.text("Well met!"));
    }

    @Override
    public void speak(PlayerCharacter pc, Component message) {
        super.speak(pc, message);
        Sound sound = Sound.sound(SoundEvent.ENTITY_VILLAGER_AMBIENT, Sound.Source.MASTER, 1f, 0.75f);
        pc.playSound(sound);
    }
}
