package com.mcquest.server.ui;

import com.mcquest.server.character.PlayerCharacter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class InteractionSequence {
    static final Duration DEFAULT_AUTO_ADVANCE_DURATION = Duration.ofSeconds(5);

    private final Interaction[] interactions;
    private final Map<PlayerCharacter, InteractionSequenceData> pcData;

    private InteractionSequence(Builder builder) {
        interactions = builder.interactions.toArray(new Interaction[0]);
        pcData = new HashMap<>();
    }

    public void advance(PlayerCharacter pc) {
        if (pc.isRemoved()) {
            return;
        }
        if (!pcData.containsKey(pc)) {
            pcData.put(pc, new InteractionSequenceData());
        }
        InteractionSequenceData data = pcData.get(pc);
        if (data.autoAdvanceTask != null) {
            data.autoAdvanceTask.cancel();
        }
        Interaction interaction = interactions[data.interactionIndex];
        interaction.onInteract.accept(pc);
        data.interactionIndex++;
        if (data.interactionIndex < interactions.length) {
            SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
            data.autoAdvanceTask = scheduler.buildTask(() -> {
                data.autoAdvanceTask = null;
                advance(pc);
            }).delay(interaction.autoAdvanceDuration).schedule();
        } else {
            pcData.remove(pc);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<Interaction> interactions;

        private Builder() {
            interactions = new ArrayList<>();
        }

        public Builder interaction(Consumer<PlayerCharacter> onInteract) {
            return interaction(onInteract, InteractionSequence.DEFAULT_AUTO_ADVANCE_DURATION);
        }

        public Builder interaction(Consumer<PlayerCharacter> onInteract,
                                   Duration autoAdvanceDuration) {
            Interaction interaction = new Interaction(onInteract, autoAdvanceDuration);
            interactions.add(interaction);
            return this;
        }

        public InteractionSequence build() {
            return new InteractionSequence(this);
        }
    }

    private static class Interaction {
        private final Consumer<PlayerCharacter> onInteract;
        private final Duration autoAdvanceDuration;

        private Interaction(Consumer<PlayerCharacter> onInteract, Duration autoAdvanceDuration) {
            this.onInteract = onInteract;
            this.autoAdvanceDuration = autoAdvanceDuration;
        }
    }

    private static class InteractionSequenceData {
        private int interactionIndex;
        private Task autoAdvanceTask;

        private InteractionSequenceData() {
            interactionIndex = 0;
            autoAdvanceTask = null;
        }
    }
}
