package net.mcquest.core.ui;

import net.kyori.adventure.text.Component;
import net.mcquest.core.character.Character;
import net.mcquest.core.character.PlayerCharacter;
import net.mcquest.core.quest.Quest;
import net.mcquest.core.quest.QuestObjective;

import java.util.function.Consumer;

public class Interactions {
    public static Consumer<PlayerCharacter> speak(
            Character character,
            Component text
    ) {
        return pc -> character.speak(pc, text);
    }

    public static Consumer<PlayerCharacter> startQuest(Quest quest) {
        return pc -> quest.start(pc);
    }

    public static Consumer<PlayerCharacter> addProgress(QuestObjective objective) {
        return pc -> objective.addProgress(pc);
    }
}
