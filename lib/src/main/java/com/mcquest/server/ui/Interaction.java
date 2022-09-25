package com.mcquest.server.ui;

import com.mcquest.server.character.PlayerCharacter;

import java.time.Duration;
import java.util.function.Consumer;

class Interaction {
    final Consumer<PlayerCharacter> onInteract;
    final Duration autoAdvanceDuration;

    Interaction(Consumer<PlayerCharacter> onInteract, Duration autoAdvanceDuration) {
        this.onInteract = onInteract;
        this.autoAdvanceDuration = autoAdvanceDuration;
    }
}
