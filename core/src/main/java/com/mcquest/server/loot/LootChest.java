package com.mcquest.server.loot;

import com.mcquest.server.audio.Sounds;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.event.EventEmitter;
import com.mcquest.server.event.LootChestOpenEvent;
import com.mcquest.server.instance.Instance;
import com.mcquest.server.particle.ParticleEffects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.hologram.Hologram;
import net.minestom.server.instance.block.Block;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collection;

public class LootChest {
    private static final int MENU_SLOTS = 27;
    private static final Duration EMIT_PARTICLE_PERIOD = Duration.ofMillis(500);

    private final Instance instance;
    private final Pos position;
    private final LootTable lootTable;
    private final EventEmitter<LootChestOpenEvent> onOpen;
    private Duration respawnDuration;
    private Hologram text;
    private Task particleEmitter;

    public LootChest(Instance instance, Pos position, LootTable lootTable) {
        this.instance = instance;
        this.position = position;
        this.lootTable = lootTable;
        onOpen = new EventEmitter<>();
        respawnDuration = null;
    }

    public Instance getInstance() {
        return instance;
    }

    public Pos getPosition() {
        return position;
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public EventEmitter<LootChestOpenEvent> onOpen() {
        return onOpen;
    }

    public @Nullable Duration getRespawnDuration() {
        return respawnDuration;
    }

    public void respawnAfter(@Nullable Duration respawnDuration) {
        this.respawnDuration = respawnDuration;
    }

    void spawn() {
        Block block = Block.CHEST;
        instance.setBlock(position, block);
        text = new Hologram(instance, position.add(0.5, 1.0, 0.5),
                Component.text("Loot Chest", NamedTextColor.GOLD));
        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        particleEmitter = scheduler.buildTask(this::emitParticles)
                .repeat(EMIT_PARTICLE_PERIOD).schedule();
    }

    // TODO: call me
    void despawn() {
        particleEmitter.cancel();
    }

    private void emitParticles() {
        double[] offset = new double[3];
        for (int i = 0; i < offset.length; i++) {
            offset[i] = 1.0 * (Math.random() - 0.5);
        }
        Pos particlePosition = position.add(0.5, 1.25, 0.5)
                .add(offset[0], offset[1], offset[2]);
        ParticleEffects.particle(instance, particlePosition, Particle.HAPPY_VILLAGER);
    }

    void open(PlayerCharacter pc) {
        Collection<Loot> loot = lootTable.generate(pc);
        LootChestOpenEvent event = new LootChestOpenEvent(pc, this, loot);
        onOpen.emit(event);
        MinecraftServer.getGlobalEventHandler().call(event);
        loot.forEach(l -> l.drop(instance, position));
        instance.playSound(Sounds.CHEST_OPEN, position);
        instance.setBlock(position, Block.AIR);
        text.remove();
        text = null;
        particleEmitter.cancel();
        particleEmitter = null;
    }

    void remove() {

    }
}
