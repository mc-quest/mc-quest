package net.mcquest.core.particle;

import net.minestom.server.MinecraftServer;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;

import java.util.HashSet;
import java.util.Set;

public class ParticleManager {
    private final Set<ParticleEmitter> emitters;

    public ParticleManager() {
        emitters = new HashSet<>();
        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        scheduler.buildTask(this::tick).repeat(TaskSchedule.nextTick()).schedule();
    }

    public void addEmitter(ParticleEmitter emitter) {
        emitters.add(emitter);
    }

    public void removeEmitter(ParticleEmitter emitter) {
        emitters.remove(emitter);
    }

    private void tick() {
        for (ParticleEmitter emitter : emitters) {
            emitter.tick();
        }
    }
}
