package com.mcquest.server.npc;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.character.CharacterModel;
import com.mcquest.core.character.NonPlayerCharacter;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.object.ObjectSpawner;
import com.mcquest.server.constants.Skins;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.sound.SoundEvent;

public class GuardThomas extends NonPlayerCharacter {
    public GuardThomas(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Skins.GUARD_MALE));
        setName("Guard Thomas");
        setLevel(10);
    }

    @Override
    protected void onInteract(PlayerCharacter pc) {
        speak(pc, Component.text("Well met!"));
    }

    @Override
    public void onSpeak(PlayerCharacter pc) {
        Sound sound = Sound.sound(SoundEvent.ENTITY_VILLAGER_AMBIENT, Sound.Source.MASTER, 1f, 0.75f);
        pc.playSound(sound);
    }
}
