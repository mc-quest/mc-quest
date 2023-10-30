package net.mcquest.server.npc;

import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.CharacterModel;
import net.mcquest.core.character.NonPlayerCharacter;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.core.quest.QuestStatus;
import net.mcquest.core.ui.InteractionSequence;
import net.mcquest.server.constants.Quests;
import net.mcquest.server.constants.Skins;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.sound.SoundEvent;

public class GuardThomas extends NonPlayerCharacter {
    private final InteractionSequence completeTutorialSequence;

    public GuardThomas(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Skins.GUARD_MALE));
        setName("Guard Thomas");
        setLevel(10);

        completeTutorialSequence = InteractionSequence.builder()
                .interaction(this::completeTutorialInteraction1)
                .interaction(this::completeTutorialInteraction2)
                .build();
    }

    @Override
    protected void onInteract(PlayerCharacter pc) {
        if (Quests.TUTORIAL.getObjective(7).isComplete(pc)
                && Quests.TUTORIAL.getStatus(pc) != QuestStatus.COMPLETED) {
            completeTutorialSequence.advance(pc);
        }
    }

    @Override
    public void onSpeak(PlayerCharacter pc) {
        Sound sound = Sound.sound(SoundEvent.ENTITY_VILLAGER_AMBIENT, Sound.Source.MASTER, 1f, 0.75f);
        pc.playSound(sound);
    }

    private void completeTutorialInteraction1(PlayerCharacter pc) {
        speak(pc, Component.text("Well done adventurer!"));
    }

    private void completeTutorialInteraction2(PlayerCharacter pc) {
        Quests.TUTORIAL.getObjective(8).addProgress(pc);
    }
}
