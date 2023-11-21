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

public class GuardThomas extends NonPlayerCharacter {
    private final InteractionSequence completeTutorialSequence;
    private final InteractionSequence startCanineCarnageSequence;
    private final InteractionSequence completeCanineCarnageSequence;

    public GuardThomas(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Skins.GUARD_MALE));
        setName("Guard Thomas");
        setLevel(10);

        completeTutorialSequence = InteractionSequence.builder()
                .interaction(Interactions.speak(this, Component.text("Well done adventurer!")))
                .interaction(Interactions.addProgress(Quests.TUTORIAL.getObjective(8)))
                .build();

        startCanineCarnageSequence = InteractionSequence.builder()
                .interaction(Interactions.speak(
                        this,
                        Component.text("Now that you're all trained up, I'll let you in on a secret.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Have you ever heard of demons?")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("I doubted the existence of such things up until recently.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("'A story for the children' I thought.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Some wolves have been attacking our camp,"
                                + " and killing all the animals in the forest.")))
                .interaction(Interactions.speak(
                        this,
                        Component.text("There's something off about them, I tell you!"
                                + " They have red eyes,"
                                + " and bite like they've never once eaten before!")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Always hungry, never fulfilled....")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Anyways, you seem strong and capable."
                                + " I'd pay good money if you managed to kill their leader.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Before you go, consider seeking out Chef Marco in the village.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("He has been in search of a skilled hunter to tackle" +
                                " the dire wolf menace lurking in our midst.")
                ))
                .interaction(Interactions.startQuest(Quests.CANINE_CARNAGE))
                .build();

        completeCanineCarnageSequence = InteractionSequence.builder()
                .interaction(Interactions.speak(this, Component.text("Woah, you survived!")))
                .interaction(Interactions.speak(
                        this,
                        Component.text("You have my thanks for culling those nasty, demonic creatures!")
                ))
                .interaction(Interactions.addProgress(Quests.CANINE_CARNAGE.getObjective(2)))
                .build();
    }

    @Override
    protected void onInteract(PlayerCharacter pc) {
        if (Quests.TUTORIAL.getObjective(7).isComplete(pc)
                && Quests.TUTORIAL.getStatus(pc) != QuestStatus.COMPLETED) {
            completeTutorialSequence.advance(pc);
        } else if (Quests.TUTORIAL.getStatus(pc) == QuestStatus.COMPLETED
                && Quests.CANINE_CARNAGE.getStatus(pc) == QuestStatus.NOT_STARTED) {
            startCanineCarnageSequence.advance(pc);
        } else if (Quests.CANINE_CARNAGE.getObjective(0).isComplete(pc)
                && Quests.CANINE_CARNAGE.getObjective(1).isComplete(pc)
                && Quests.CANINE_CARNAGE.getStatus(pc) != QuestStatus.COMPLETED) {
            completeCanineCarnageSequence.advance(pc);
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
