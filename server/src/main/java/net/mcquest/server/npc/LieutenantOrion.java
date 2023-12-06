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

public class LieutenantOrion extends NonPlayerCharacter {
    private final InteractionSequence progressItsyBitsyQuests;

    public LieutenantOrion(Mmorpg mmorpg, ObjectSpawner spawner) {
        super(mmorpg, spawner, CharacterModel.of(Skins.LIEUTENANT_ORION));
        setName("Lieutenant Orion");
        setLevel(5);

        // completes "Itsy Bitsy Spider" quest and starts "Not So Itsy Bitsy" quest
        progressItsyBitsyQuests = InteractionSequence.builder()
                .interaction(Interactions.speak(
                        this,
                        Component.text("Oh, thank the stars you're here! " +
                                "Who... who are you? What's your purpose in this dungeon?")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Wait, Seraphina sent you, right? Great, great.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("So, here's the deal – the spiders, they're not itsy bitsy." +
                                " There's a massive broodmother down there, and I'm having second thoughts.")
                ))
                .interaction(Interactions.speak(
                        this,
                        Component.text("Mind helping me out? Village promised we'd clean this mess up," +
                                " and I don't want to admit I might've exaggerated my bravery a bit.")
                ))
                .interaction(Interactions.addProgress(Quests.ITSY_BITSY_SPIDER.getObjective(2)))
                .interaction(Interactions.startQuest(Quests.NOT_SO_ITSY_BITSY))
                .build();
    }

    @Override
    protected void onInteract(PlayerCharacter pc) {
        lookAt(pc);
        if (Quests.ITSY_BITSY_SPIDER.getObjective(2).isInProgress(pc)) {
            progressItsyBitsyQuests.advance(pc);
        } else if (Quests.NOT_SO_ITSY_BITSY.getObjective(0).isInProgress(pc)) {
            speak(pc, Component.text("Good luck fending off that broodmother."));
        } else if (Quests.NOT_SO_ITSY_BITSY.getObjective(0).isComplete(pc)) {
            speak(pc, Component.text("WOW! You defeated the broodmother?"));
        } else if (Quests.NOT_SO_ITSY_BITSY.getObjective(2).isComplete(pc)) {
            speak(pc, Component.text("Right, I should be getting back to Oakshire. Thanks again!"));
        } else {
            speak(pc, Component.text("Who are you?"));
        }
    }

    @Override
    public void onSpeak(PlayerCharacter pc) {
        Sound sound = Sound.sound(SoundEvent.ENTITY_VILLAGER_AMBIENT, Sound.Source.MASTER, 1f, 0.75f);
        pc.playSound(sound);
    }
}
