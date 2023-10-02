package com.mcquest.server.npc;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.ai.*;
import com.mcquest.core.character.CharacterModel;
import com.mcquest.core.character.NonPlayerCharacter;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.object.ObjectSpawner;
import com.mcquest.server.constants.Skins;
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

        setBrain(new Sequence(
                new TaskWait(Duration.ofSeconds(2)),
                new Parallel(
                        Parallel.Policy.REQUIRE_ONE,
                        Parallel.Policy.REQUIRE_ONE,
                        new TaskGoToRandomPosition(10),
                        new LoopForever(new Sequence(
                                new TaskPlaySound(Sound.sound(SoundEvent.BLOCK_GRASS_STEP,
                                        Sound.Source.AMBIENT, 1f, 1f)),
                                new TaskWait(Duration.ofMillis(500))
                        ))
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
