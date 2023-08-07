package com.mcquest.core.loot;

import com.mcquest.core.audio.Sounds;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.event.EventEmitter;
import com.mcquest.core.event.LootChestOpenEvent;
import com.mcquest.core.event.LootChestRespawnEvent;
import com.mcquest.core.instance.Instance;
import com.mcquest.core.model.CoreModels;
import com.mcquest.core.object.Object;
import com.mcquest.core.particle.ParticleEffects;
import com.mcquest.core.util.MathUtility;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.hologram.Hologram;
import net.minestom.server.entity.metadata.other.ArmorStandMeta;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.Nullable;
import team.unnamed.hephaestus.minestom.ModelEntity;

import java.time.Duration;
import java.util.Collection;

public class LootChest extends Object {
    private static final Duration EMIT_PARTICLE_PERIOD = Duration.ofMillis(500);

    private final LootTable lootTable;
    private final EventEmitter<LootChestOpenEvent> onOpen;
    private final EventEmitter<LootChestRespawnEvent> onRespawn;
    private Duration respawnDuration;
    private boolean opened;
    private Entity entity;
    private Hologram text;
    private Task particleEmitter;

    public LootChest(Instance instance, Pos position, LootTable lootTable) {
        super(instance, position);
        this.lootTable = lootTable;
        this.onOpen = new EventEmitter<>();
        this.onRespawn = new EventEmitter<>();
        respawnDuration = null;
        opened = false;
    }

    LootChest(LootChest lootChest) {
        super(lootChest.getInstance(), lootChest.getPosition());
        lootTable = lootChest.lootTable;
        onOpen = lootChest.onOpen;
        onRespawn = lootChest.onRespawn;
        respawnDuration = lootChest.respawnDuration;
        opened = false;
    }

    @Override
    public void setInstance(Instance instance, Pos position) {
        super.setInstance(instance, position);

        if (!isSpawned()) {
            return;
        }

        entity.setInstance(instance, position);

        text.remove();
        text = createText();
    }

    @Override
    public void setPosition(Pos position) {
        super.setPosition(position);

        if (!isSpawned()) {
            return;
        }

        entity.teleport(position);

        text.setPosition(textPosition());
    }

    public LootTable getLootTable() {
        return lootTable;
    }

    public EventEmitter<LootChestOpenEvent> onOpen() {
        return onOpen;
    }

    public EventEmitter<LootChestRespawnEvent> onRespawn() {
        return onRespawn;
    }

    public Duration getRespawnDuration() {
        return respawnDuration;
    }

    public void setRespawnDuration(@Nullable Duration respawnDuration) {
        this.respawnDuration = respawnDuration;
    }

    public boolean isOpened() {
        return opened;
    }

    @Override
    protected void spawn() {
        super.spawn();

        Instance instance = getInstance();
        Pos position = getPosition();

        entity = new Entity(this);
        entity.setInstance(instance, position);
        entity.setNoGravity(true);
        entity.playAnimation("bounce");

        text = createText();

        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        particleEmitter = scheduler.buildTask(this::emitParticles)
                .repeat(EMIT_PARTICLE_PERIOD).schedule();
    }

    private Hologram createText() {
        Hologram text = new Hologram(getInstance(), textPosition(),
                Component.text("Loot Chest", NamedTextColor.GREEN));
        ((ArmorStandMeta) text.getEntity().getEntityMeta()).setMarker(true);
        return text;
    }

    private Pos textPosition() {
        return getPosition().withY(y -> y + 2.25);
    }

    @Override
    protected void despawn() {
        super.despawn();
        entity.remove();
        entity = null;

        text.remove();
        text = null;

        particleEmitter.cancel();
    }

    private void emitParticles() {
        Instance instance = getInstance();
        Pos position = getPosition();
        Pos particlePosition = position.add(randomOffset(), 1.25 + randomOffset(), randomOffset());
        ParticleEffects.particle(instance, particlePosition, Particle.HAPPY_VILLAGER);
    }

    private double randomOffset() {
        return MathUtility.randomRange(-0.5, 0.5);
    }

    void open(PlayerCharacter pc) {
        Instance instance = getInstance();
        Pos position = getPosition();

        Collection<Loot> loot = lootTable.generate(pc);

        LootChestOpenEvent event = new LootChestOpenEvent(pc, this, loot);
        onOpen.emit(event);
        MinecraftServer.getGlobalEventHandler().call(event);

        opened = true;

        entity.playAnimation("open");

        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();

        scheduler.buildTask(() -> instance.playSound(Sounds.CHEST_OPEN, position))
                .delay(TaskSchedule.millis(200)).schedule();

        scheduler.buildTask(() ->
                loot.forEach(l -> l.drop(instance, lootPosition()))
        ).delay(TaskSchedule.millis(400)).schedule();

        scheduler.buildTask(() -> {
            poof();
            remove();
        }).delay(TaskSchedule.millis(2000)).schedule();
    }

    private Pos lootPosition() {
        return getPosition().add(randomOffset(), randomOffset() + 0.75, randomOffset());
    }

    private void poof() {
        for (int i = 0; i < 5; i++) {
            Pos particlePosition = getPosition()
                    .add(randomOffset(), randomOffset() + 0.75, randomOffset());
            ParticleEffects.particle(getInstance(), particlePosition, Particle.POOF);
        }
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
