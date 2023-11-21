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

public class ChefMarco extends NonPlayerCharacter {
    private final InteractionSequence startWolfBiteDelightSequence;
    private final InteractionSequence completeWolfBiteDelightSequence;

    public ChefMarco(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Skins.CHEF_MARCO));
        setName("Chef Marco");
        setLevel(5);

        startWolfBiteDelightSequence = InteractionSequence.builder()
                .interaction(Interactions.speak(
                        this,
                        Component.text("Greetings, adventurer. Your demeanor speaks of capability.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("I am Chef Marco, culinary maestro of this Outpost. " +
                                "Can you lend your skills to a culinary quest?")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("I seek a rare ingredient for a dish of mythical proportions – " +
                                "the wolf bite flank.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Five of these succulent morsels shall suffice. " +
                                "Venture to the wild outskirts to claim them.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("The wolves there possess the essence needed to " +
                                "transform a mundane meal into a feast fit for heroes.")
                ))
                .interaction(Interactions.startQuest(Quests.WOLF_BITE_DELIGHT))
                .build();

        completeWolfBiteDelightSequence = InteractionSequence.builder()
                .interaction(Interactions.speak(
                        this,
                        Component.text("Ah, the winds carry tales of your return. " +
                                "Have you, by chance, secured the coveted wolf bite flanks?")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Marvelous! These morsels shall weave magic into" +
                                " my culinary creation, a delight for the brave.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Your contribution is as a melody in the grand symphony." +
                                " Should you hunger for a hero's repast, seek me out.")
                ))
                .interaction(Interactions.addProgress(Quests.WOLF_BITE_DELIGHT.getObjective(1)))
                .build();
    }

    @Override
    protected void onInteract(PlayerCharacter pc) {
        if (Quests.TUTORIAL.getStatus(pc) == QuestStatus.COMPLETED
            && Quests.WOLF_BITE_DELIGHT.getStatus(pc) == QuestStatus.NOT_STARTED) {
            startWolfBiteDelightSequence.advance(pc);
        } else if (Quests.WOLF_BITE_DELIGHT.getObjective(0).isComplete(pc)
            && Quests.WOLF_BITE_DELIGHT.getStatus(pc) != QuestStatus.COMPLETED) {
            completeWolfBiteDelightSequence.advance(pc);
        } else if (Quests.WOLF_BITE_DELIGHT.getStatus(pc) == QuestStatus.COMPLETED) {
            speak(pc, Component.text("I extend my gratitude for your earlier aid. " +
                    "Should the need for your assistance arise anew, the winds of fate shall guide you to me."));
        } else {
            speak(pc, Component.text("Pray, do you possess any enchanting ingredients?"));
        }
    }

    @Override
    public void onSpeak(PlayerCharacter pc) {
        Sound sound = Sound.sound(SoundEvent.ENTITY_VILLAGER_AMBIENT, Sound.Source.MASTER, 1f, 0.75f);
        pc.playSound(sound);
    }
}
