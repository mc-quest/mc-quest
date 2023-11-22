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
import net.mcquest.core.ui.Interactions;

public class GuardAnya extends NonPlayerCharacter {
    private final InteractionSequence completeDreadfangsRevengeSequence;
    private final InteractionSequence startDreadfangsRevengeSequence;

    public GuardAnya(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Skins.VILLAGER_FEMALE));
        setName("Guard Anya");
        setLevel(10);

        completeDreadfangsRevengeSequence = InteractionSequence.builder()
                .interaction(Interactions.speak(this, Component.text("WHAT? You actually"
                        + " managed to kill the goblin king?")
                ))
                .interaction(Interactions.speak(this, Component.text("Well, you have my thanks!"
                        + " and my respect!")
                ))
                .interaction(Interactions.addProgress(Quests.DREADFANGS_REVENGE.getObjective(3)))
                .build();

        startDreadfangsRevengeSequence = InteractionSequence.builder()
                .interaction(Interactions.speak(
                        this,
                        Component.text("Woah there adventurer! You startled me!")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("My name is Anya. This city here used to be my home before"
                                + " it was overtaken by the goblin king.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Of course, it's been many years since that happened,"
                                + " I was only a child then... but I've never forgiven him.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text(" I've killed many of his minions, but I can't seem to"
                                + "get to Grimrot himself.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text(" Oh, you want to give it a go, adventurer?")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text(" Ha! Good Luck!")
                ))
                .interaction(Interactions.startQuest(Quests.DREADFANGS_REVENGE))
                .build();
    }

    @Override
    protected void onInteract(PlayerCharacter pc) {
        if (Quests.DREADFANGS_REVENGE.getStatus(pc) == QuestStatus.NOT_STARTED) {
            startDreadfangsRevengeSequence.advance(pc);
        } else if (Quests.DREADFANGS_REVENGE.getStatus(pc) != QuestStatus.COMPLETED
                && !Quests.DREADFANGS_REVENGE.getObjective(3).isComplete(pc)) {
            completeDreadfangsRevengeSequence.advance(pc);
        } else {
            speak(pc, Component.text("Need something adventurer?"));
        }
    }

    @Override
    public void onSpeak(PlayerCharacter pc) {
        Sound sound = Sound.sound(SoundEvent.ENTITY_VILLAGER_AMBIENT, Sound.Source.MASTER, 1f, 0.75f);
        pc.playSound(sound);
    }
}
