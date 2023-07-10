package com.mcquest.core.playerclass;

import com.mcquest.core.Mmorpg;
import com.mcquest.core.audio.Sounds;
import com.mcquest.core.character.PlayerCharacter;
import com.mcquest.core.character.PlayerCharacterManager;
import com.mcquest.core.event.ActiveSkillUseEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerChangeHeldSlotEvent;
import net.minestom.server.inventory.PlayerInventory;
import net.minestom.server.item.ItemStack;
import net.minestom.server.timer.SchedulerManager;
import net.minestom.server.timer.TaskSchedule;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The PlayerClassManager is used to register and retrieve PlayerClasses.
 */
public class PlayerClassManager {
    private final Mmorpg mmorpg;
    private final Map<Integer, PlayerClass> playerClassesById;

    @ApiStatus.Internal
    public PlayerClassManager(Mmorpg mmorpg, PlayerClass[] playerClasses) {
        this.mmorpg = mmorpg;
        playerClassesById = new HashMap<>();
        for (PlayerClass playerClass : playerClasses) {
            registerPlayerClass(playerClass);
        }

        GlobalEventHandler eventHandler = MinecraftServer.getGlobalEventHandler();
        eventHandler.addListener(PlayerChangeHeldSlotEvent.class, this::handleChangeHeldSlot);

        SchedulerManager scheduler = MinecraftServer.getSchedulerManager();
        scheduler.buildTask(this::tickSkillCooldowns).repeat(TaskSchedule.nextTick()).schedule();
    }

    private void registerPlayerClass(PlayerClass playerClass) {
        int id = playerClass.getId();
        if (playerClassesById.containsKey(id)) {
            throw new IllegalArgumentException("ID already in use: " + id);
        }
        playerClassesById.put(playerClass.getId(), playerClass);
    }

    private void handleChangeHeldSlot(PlayerChangeHeldSlotEvent event) {
        int slot = event.getSlot();
        if (!(slot >= SkillManager.MIN_HOTBAR_SLOT && slot <= SkillManager.MAX_HOTBAR_SLOT)) {
            return;
        }

        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        ItemStack itemStack = inventory.getItemStack(slot);
        PlayerCharacter pc = mmorpg.getPlayerCharacterManager().getPlayerCharacter(player);
        SkillManager skillManager = pc.getSkillManager();

        ActiveSkill skill = (ActiveSkill) skillManager.getSkill(itemStack);
        if (skill == null) {
            return;
        }

        handleUseSkill(pc, skill);
    }

    private void handleUseSkill(PlayerCharacter pc, ActiveSkill skill) {
        pc.playSound(Sounds.CLICK);

        pc.getMapViewer().close();

        double manaCost = skill.getManaCost();
        if (pc.getMana() < skill.getManaCost()) {
            pc.sendMessage(Component.text("Not enough mana", NamedTextColor.RED));
            return;
        }
        if (!skill.getCooldown(pc).isZero()) {
            pc.sendMessage(Component.text("On cooldown", NamedTextColor.RED));
            return;
        }
        ActiveSkillUseEvent event = new ActiveSkillUseEvent(pc, skill);
        skill.onUse().emit(event);
        mmorpg.getGlobalEventHandler().call(event);
        if (!event.isCancelled()) {
            pc.removeMana(manaCost);
            pc.getSkillManager().startCooldown(skill);
        }
    }

    /**
     * Returns the PlayerClass with the given name.
     */
    public PlayerClass getPlayerClass(int id) {
        return playerClassesById.get(id);
    }

    public Collection<PlayerClass> getPlayerClasses() {
        return Collections.unmodifiableCollection(playerClassesById.values());
    }

    private void tickSkillCooldowns() {
        PlayerCharacterManager pcManager = mmorpg.getPlayerCharacterManager();
        for (PlayerCharacter pc : pcManager.getPlayerCharacters()) {
            SkillManager skillManager = pc.getSkillManager();
            skillManager.tickSkillCooldowns();
        }
    }
}
