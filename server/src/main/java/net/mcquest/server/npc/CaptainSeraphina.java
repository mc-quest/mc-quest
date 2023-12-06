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

public class CaptainSeraphina extends NonPlayerCharacter {
    private final InteractionSequence startItsyBitsySpiderSequence;
    private final InteractionSequence completeNotSoItsyBitsySequence;

    public CaptainSeraphina(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Skins.CAPTAIN_SERAPHINA));
        setName("Captain Seraphina");
        setLevel(5);

        startItsyBitsySpiderSequence = InteractionSequence.builder()
                .interaction(Interactions.speak(
                        this,
                        Component.text("Hey there! Quick favor if you've got a moment.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("My sibling, Orion, went with a group of other fishermen to" +
                                " figure out the sudden spawn of spiders in the Ashen Tangle.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Says it's not a big deal, but I need someone to check on them." +
                                " Give 'em a nudge back home, would ya?")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("It's been a while since he left, and we're getting worried.")
                ))
                .interaction(Interactions.startQuest(Quests.ITSY_BITSY_SPIDER))
                .build();

        completeNotSoItsyBitsySequence = InteractionSequence.builder()
                .interaction(Interactions.speak(
                        this,
                        Component.text("Back so soon? How's my sibling faring in the Ashen Tangle?")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Please tell me those spiders weren't as menacing as they sounded.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("You did? Oh, stars! I knew Orion was in over their head.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("You've got my eternal gratitude. What happened down there?")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Well, whatever it is, let's focus on dealing with the spider threat.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Keep me posted on your progress, and stay safe out there.")
                ))
                .interaction(Interactions.addProgress(Quests.NOT_SO_ITSY_BITSY.getObjective(3)))
                .build();
    }

    @Override
    protected void onInteract(PlayerCharacter pc) {
        lookAt(pc);
        if (
                // TODO: add quest pre-reqs later
                //Quests.TUTORIAL.getStatus(pc) == QuestStatus.COMPLETED &&
            Quests.ITSY_BITSY_SPIDER.getStatus(pc) == QuestStatus.NOT_STARTED) {
                startItsyBitsySpiderSequence.advance(pc);
        } else if (Quests.NOT_SO_ITSY_BITSY.getObjective(2).isComplete(pc) &&
                     Quests.NOT_SO_ITSY_BITSY.getObjective(3).isInProgress(pc)) {
            completeNotSoItsyBitsySequence.advance(pc);
        } else if (Quests.ITSY_BITSY_SPIDER.isInProgress(pc) || Quests.NOT_SO_ITSY_BITSY.isInProgress(pc)) {
            speak(pc, Component.text("Did you find Orion?"));
        } else if (Quests.NOT_SO_ITSY_BITSY.getStatus(pc) == QuestStatus.COMPLETED) {
            speak(pc, Component.text("My thanks for your earlier help. If ever you find yourself in need" +
                    " again, let the currents of fortune bring you back to my shores."));
        } else {
            speak(pc, Component.text("Hey there, stranger! Tight lines and fair seas to ya!"));
        }
    }

    @Override
    public void onSpeak(PlayerCharacter pc) {
        Sound sound = Sound.sound(SoundEvent.ENTITY_VILLAGER_AMBIENT, Sound.Source.MASTER, 1f, 0.75f);
        pc.playSound(sound);
    }
}
