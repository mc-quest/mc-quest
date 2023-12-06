package net.mcquest.server.npc;
import net.mcquest.core.Mmorpg;
import net.mcquest.core.character.CharacterModel;
import net.mcquest.core.character.NonPlayerCharacter;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.object.ObjectSpawner;
import net.mcquest.core.ui.InteractionSequence;
import net.mcquest.server.constants.Quests;
import net.mcquest.server.constants.Skins;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.minestom.server.sound.SoundEvent;
import net.mcquest.core.ui.Interactions;

public class OutsideFishermen extends NonPlayerCharacter {
    private final InteractionSequence progressItsyBitsySpiderSequence;
    private final InteractionSequence progressNotSoItsyBitsySequence;

    public OutsideFishermen(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Skins.FISHER_MALE));
        setName("Fishermen");
        setLevel(4);

        progressItsyBitsySpiderSequence = InteractionSequence.builder()
                .interaction(Interactions.speak(
                        this,
                        Component.text("Hold up, traveler! You heading into that spider-infested dungeon?")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Aye, I'm one of them fishermen who went in with Orion and the others." +
                                " Nasty business in there, let me tell ya.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("I decided to stay out, just in case they don't make it back." +
                                " If you're heading in, be careful. We can't afford to lose more folks.")
                ))
                .interaction(Interactions.addProgress(Quests.ITSY_BITSY_SPIDER.getObjective(0)))
                .build();

        progressNotSoItsyBitsySequence = InteractionSequence.builder()
                .interaction(Interactions.speak(
                        this,
                        Component.text("Wow, you defeated that monstrosity?")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("I'll go let Orion know. He'll be thrilled to hear the good news.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Safe travels, adventurer!")
                ))
                .interaction(Interactions.addProgress(Quests.NOT_SO_ITSY_BITSY.getObjective(2)))
                .build();
    }

    @Override
    protected void onInteract(PlayerCharacter pc) {
        lookAt(pc);

        if (Quests.ITSY_BITSY_SPIDER.getObjective(0).isInProgress(pc)) {
            progressItsyBitsySpiderSequence.advance(pc);
        } else if (Quests.NOT_SO_ITSY_BITSY.getObjective(1).isComplete(pc) &&
                    Quests.NOT_SO_ITSY_BITSY.getObjective(2).isInProgress(pc)) {
            progressNotSoItsyBitsySequence.advance(pc);
        } else if (Quests.ITSY_BITSY_SPIDER.isInProgress(pc) ||
                    Quests.NOT_SO_ITSY_BITSY.isInProgress(pc)) {
            speak(pc, Component.text("Hows your progress?"));
        } else {
            speak(pc, Component.text("Hello, traveler."));
        }
    }

    @Override
    public void onSpeak(PlayerCharacter pc) {
        Sound sound = Sound.sound(SoundEvent.ENTITY_VILLAGER_AMBIENT, Sound.Source.MASTER, 1f, 0.75f);
        pc.playSound(sound);
    }
}
