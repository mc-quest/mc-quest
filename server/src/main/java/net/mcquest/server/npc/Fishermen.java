package net.mcquest.server.npc;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.ai.*;
import net.mcquest.core.character.CharacterModel;
import net.mcquest.core.character.NonPlayerCharacter;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.server.constants.Quests;
import net.mcquest.server.constants.Skins;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;

public class Fishermen extends NonPlayerCharacter {

    public Fishermen(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(skin()));
        setName("Fishermen");
        setLevel(4);
        setMovementSpeed(2.0);

        setBrain(Sequence.of(
                TaskWait.of(Duration.ofSeconds(2)),
                SimpleParallel.of(
                        TaskGoToRandomPosition.of(10),
                        Sequence.of(
                                TaskEmitSound.of(Sound.sound(SoundEvent.BLOCK_GRASS_STEP,
                                        Sound.Source.AMBIENT, 1f, 1f)),
                                TaskWait.of(Duration.ofMillis(500))
                        )
                )
        ));
    }

    private static PlayerSkin skin() {
        return Math.random() < 0.5
                ? Skins.FISHER_MALE
                : Skins.FISHER_FEMALE;
    }

    @Override
    protected void onInteract(PlayerCharacter pc) {
        lookAt(pc);
        if (Quests.NOT_SO_ITSY_BITSY.getObjective(0).isComplete(pc)) {
            speak(pc, Component.text("WE GET TO LEAVE?! You don't have to tell me twice."));
        } else {
            speak(pc, Component.text("I shouldn't have listened to Orion. " +
                                            "I just want to get out of this place."));
        }
    }

    @Override
    public void onSpeak(PlayerCharacter pc) {
        Sound sound = Sound.sound(SoundEvent.ENTITY_VILLAGER_AMBIENT, Sound.Source.MASTER, 1f, 0.75f);
        pc.playSound(sound);
    }
}