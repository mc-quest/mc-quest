package com.mcquest.core.loot;

import com.mcquest.core.audio.Sounds;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.event.EventEmitter;
import com.mcquest.core.event.LootChestOpenEvent;
import com.mcquest.core.instance.Instance;
import com.mcquest.core.model.CoreModels;
import com.mcquest.core.object.Object;
import com.mcquest.core.particle.ParticleEffects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.hologram.Hologram;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import team.unnamed.hephaestus.minestom.ModelEntity;

import java.time.Duration;
import java.util.Collection;

public class LootChest extends Object {
    private static final Duration EMIT_PARTICLE_PERIOD = Duration.ofMillis(500);

    private final LootTable lootTable;
    private final EventEmitter<LootChestOpenEvent> onOpen;
    private Entity entity;
    private Hologram text;
    private Task particleEmitter;

    public LootChest(Instance instance, Pos position, LootTable lootTable) {
        super(instance, position);
        this.lootTable = lootTable;
        onOpen = new EventEmitter<>();
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public EventEmitter<LootChestOpenEvent> onOpen() {
        return onOpen;
    }

    @Override
    protected void spawn() {
        super.spawn();
        Instance instance = getInstance();
        Pos position = getPosition();
        entity = new Entity(this);
        entity.setInstance(instance, position);
        entity.setNoGravity(true);
        text = new Hologram(instance, position.withY(y -> y + 2.0),
                Component.text("Loot Chest", NamedTextColor.GOLD));
        ((ArmorStandMeta) text.getEntity().getEntityMeta()).setMarker(true);
        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        particleEmitter = scheduler.buildTask(this::emitParticles)
                .repeat(EMIT_PARTICLE_PERIOD).schedule();
    }

    @Override
    protected void despawn() {
        super.despawn();
        entity.remove();
        text.remove();
        particleEmitter.cancel();
    }

    private void emitParticles() {
        Instance instance = getInstance();
        Pos position = getPosition();
        Pos particlePosition = position.add(0.5, 1.25, 0.5)
                .add(randomOffset(), randomOffset(), randomOffset());
        ParticleEffects.particle(instance, particlePosition, Particle.HAPPY_VILLAGER);
    }

    private double randomOffset() {
        return 1.0 * (Math.random() - 0.5);
    }

    void open(PlayerCharacter pc) {
        Instance instance = getInstance();
        Pos position = getPosition();

        Collection<Loot> loot = lootTable.generate(pc);

        LootChestOpenEvent event = new LootChestOpenEvent(pc, this, loot);
        onOpen.emit(event);
        MinecraftServer.getGlobalEventHandler().call(event);

        loot.forEach(l -> l.drop(instance, position));

        instance.playSound(Sounds.CHEST_OPEN, position);

        remove();
    }

    class Entity extends ModelEntity {
        private final LootChest lootChest;

        private Entity(LootChest lootChest) {
            super(CoreModels.LOOT_CHEST);
            this.lootChest = lootChest;
        }

        public LootChest getLootChest() {
            return lootChest;
        }
    }
}
