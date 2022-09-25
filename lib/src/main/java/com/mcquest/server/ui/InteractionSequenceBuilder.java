package com.mcquest.server.ui;

import com.mcquest.server.character.PlayerCharacter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InteractionSequenceBuilder {
    List<Interaction> interactions;

    InteractionSequenceBuilder() {
        interactions = new ArrayList<>();
    }

    public InteractionSequenceBuilder interaction(Consumer<PlayerCharacter> onInteract) {
        return interaction(onInteract, InteractionSequence.DEFAULT_AUTO_ADVANCE_DURATION);
    }

    public InteractionSequenceBuilder interaction(Consumer<PlayerCharacter> onInteract,
                                                  Duration autoAdvanceDuration) {
        Interaction interaction = new Interaction(onInteract, autoAdvanceDuration);
        interactions.add(interaction);
        return this;
    }

    public InteractionSequence build() {
        return new InteractionSequence(this);
    }
}
