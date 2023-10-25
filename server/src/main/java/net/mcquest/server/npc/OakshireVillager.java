package net.mcquest.server.npc;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.ai.*;
import net.mcquest.core.character.CharacterModel;
import net.mcquest.core.character.NonPlayerCharacter;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.server.constants.Skins;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.sound.SoundEvent;

import java.time.Duration;

public class OakshireVillager extends NonPlayerCharacter {
    public OakshireVillager(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(skin()));
        setName("Oakshire Villager");
        setLevel(1);
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
                ? Skins.VILLAGER_MALE
                : Skins.VILLAGER_FEMALE;
    }

    @Override
    protected void onInteract(PlayerCharacter pc) {
        lookAt(pc);
        speak(pc, Component.text("Well met, adventurer!"));
    }
}
