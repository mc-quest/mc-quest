package com.mcquest.server.loot;

import com.mcquest.server.audio.Sounds;
import com.mcquest.server.character.PlayerCharacter;
import com.mcquest.server.event.EventEmitter;
import com.mcquest.server.event.LootChestCloseEvent;
import com.mcquest.server.event.LootChestOpenEvent;
import com.mcquest.server.instance.Instance;
import com.mcquest.server.particle.ParticleEffects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.hologram.Hologram;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.ItemStack;
import net.minestom.server.particle.Particle;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.Task;

import java.time.Duration;
import java.util.*;

public class LootChest {
    private static final int MENU_SLOTS = 27;
    private static final Duration EMIT_PARTICLE_PERIOD = Duration.ofMillis(500);

    private final Instance instance;
    private final Pos position;
    private final LootTable lootTable;
    private final EventEmitter<LootChestOpenEvent> onOpen;
    private final EventEmitter<LootChestCloseEvent> onClose;
    private Hologram text;
    private Task particleEmitter;

    public LootChest(Instance instance, Pos position, LootTable lootTable) {
        this.instance = instance;
        this.position = position;
        this.lootTable = lootTable;
        // TODO CHECK THAT LOOT TABLE WILL NOT EXCEED MENU_SLOTS
        onOpen = new EventEmitter<>();
        onClose = new EventEmitter<>();
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

    public EventEmitter<LootChestCloseEvent> onClose() {
        return onClose;
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
        Loot[] lootBySlot = generateLoot(pc);
        LootChestOpenEvent event = new LootChestOpenEvent(pc, this, lootBySlot);
        onOpen().emit(event);
        Inventory menu = createMenu(pc, lootBySlot);
        pc.getPlayer().openInventory(menu);
        instance.playSound(Sounds.CHEST_OPEN, position);
        instance.setBlock(position, Block.AIR);
        text.remove();
        text = null;
        particleEmitter.cancel();
        particleEmitter = null;
    }

    void remove() {

    }

    private Loot[] generateLoot(PlayerCharacter pc) {
        Integer[] slots = new Integer[MENU_SLOTS];
        for (int i = 0; i < MENU_SLOTS; i++) {
            slots[i] = i;
        }
        Collections.shuffle(Arrays.asList(slots));

        Collection<Loot> loot = lootTable.generate(pc);
        Loot[] lootBySlot = new Loot[MENU_SLOTS];

        int i = 0;
        for (Loot l : loot) {
            lootBySlot[slots[i]] = l;
            i++;
        }

        return lootBySlot;
    }

    private Inventory createMenu(PlayerCharacter pc, Loot[] lootBySlot) {
        Inventory inventory = new Inventory(InventoryType.CHEST_3_ROW, "Loot Chest");
        for (int i = 0; i < MENU_SLOTS; i++) {
            Loot lootInstance = lootBySlot[i];
            if (lootInstance != null) {
                inventory.setItemStack(i, lootInstance.getItemStack());
            }
        }

        inventory.addInventoryCondition((player, slot, clickType, result) -> {
            Loot lootInstance = lootBySlot[slot];
            if (lootInstance != null) {
                ItemStack remains = lootInstance.loot(pc);
                inventory.setItemStack(slot, remains);
            }
            result.setCancel(true);
        });

        return inventory;
    }
}
