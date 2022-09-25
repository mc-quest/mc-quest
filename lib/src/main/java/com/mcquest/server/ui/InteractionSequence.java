package com.mcquest.server.ui;

import com.mcquest.server.character.PlayerCharacter;
import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class InteractionSequence {
    static final Duration DEFAULT_AUTO_ADVANCE_DURATION = Duration.ofSeconds(5);

    private final Interaction[] interactions;
    private final Map<PlayerCharacter, InteractionSequenceData> pcData;

    InteractionSequence(InteractionSequenceBuilder builder) {
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

    public static InteractionSequenceBuilder builder() {
        return new InteractionSequenceBuilder();
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
