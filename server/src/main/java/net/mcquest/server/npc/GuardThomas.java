package net.mcquest.server.npc;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.CharacterModel;
import net.mcquest.core.character.NonPlayerCharacter;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.server.constants.Skins;
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
